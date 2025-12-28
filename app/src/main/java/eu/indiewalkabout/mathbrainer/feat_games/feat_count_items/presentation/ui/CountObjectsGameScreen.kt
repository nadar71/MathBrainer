package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.components.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.presentation.components.ResultBanner
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.components.CountObjectsHeader
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.components.Placement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun CountObjectsGameScreen(
    initialHighScore: Int = 0,
    onBack: () -> Unit,
    viewModel: CountObjectsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(initialHighScore) {
        viewModel.startGame(initialHighScore)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.onQuitGame()
                    onBack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_back),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.quick_count_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(id = R.string.quick_count_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CountObjectsHeader(
                state = state,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            MemorizeCanvas(
                itemCount = state.challenge?.itemsToCount ?: 0,
                isVisible = state.isShowingItems,
                challengeKey = state.challengeId
            )

            when (state.feedback) {
                ChallengeUiState.Feedback.SUCCESS -> ResultBanner(
                    text = stringResource(id = R.string.ok_str),
                    color = MaterialTheme.colorScheme.primary
                )

                ChallengeUiState.Feedback.FAILURE -> ResultBanner(
                    text = stringResource(id = R.string.wrong_answer),
                    color = MaterialTheme.colorScheme.error
                )
                null -> Unit
            }

            AnswersGrid (
                options = state.challenge?.answerOptions.orEmpty(),
                enabled = !state.isShowingItems && !state.isGameOver,
                onOptionSelected = { viewModel.submitAnswer(it) }
            )

            if (state.showNextButton && !state.isGameOver) {
                Button(
                    onClick = { viewModel.onNextChallenge() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.quick_count_relaunch))
                }
            }
        }
    }

    if (state.isGameOver) {
        GameOverDialog(onDismiss = {
            viewModel.onQuitGame()
            onBack()
        })
    }
}



@Composable
private fun MemorizeCanvas(
    itemCount: Int,
    isVisible: Boolean,
    challengeKey: Int,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
) {
    val context = LocalContext.current
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var memoImages by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }

    LaunchedEffect(itemCount) {
        memoImages = loadMemoImages(context, max(itemCount, MIN_IMAGES))
    }

    val placements = remember(challengeKey, canvasSize, memoImages, isVisible) {
        if (!isVisible || canvasSize == IntSize.Zero || memoImages.isEmpty()) return@remember emptyList()
        generatePlacements(itemCount, canvasSize, memoImages)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .onSizeChanged { canvasSize = it }
        ) {
            if (isVisible) {
                placements.forEach { placement ->
                    drawImage(
                        image = placement.image,
                        topLeft = placement.offset
                    )
                }
            }
        }

        if (!isVisible) {
            Text(
                text = stringResource(id = R.string.count_objects_question),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}


@Composable
private fun AnswersGrid(
    options: List<Int>,
    enabled: Boolean,
    onOptionSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowOptions.forEach { value ->
                    Button(
                        onClick = { onOptionSelected(value) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = enabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                repeat(max(0, 2 - rowOptions.size)) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun generatePlacements(
    itemCount: Int,
    canvasSize: IntSize,
    memoImages: List<ImageBitmap>,
    scale: Float = 0.2f
): List<Placement> {
    val placements = mutableListOf<Placement>()
    val maxWidth = canvasSize.width
    val maxHeight = canvasSize.height

    repeat(itemCount) { index ->
        val baseImage = memoImages[index % memoImages.size]
        val scaledImage = baseImage.scale(scale)
        val scaledWidth = scaledImage.width
        val scaledHeight = scaledImage.height
        var attempts = 0
        var position: Offset
        do {
            val xRange = (maxWidth - scaledWidth - 8).coerceAtLeast(1)
            val yRange = (maxHeight - scaledHeight - 8).coerceAtLeast(1)
            val x = Random.nextInt(0, xRange) + 4
            val y = Random.nextInt(0, yRange) + 4
            position = Offset(x.toFloat(), y.toFloat())
            attempts++
        } while (isOverlapping(position, scaledWidth, scaledHeight, placements) && attempts < 30)

        placements.add(
            Placement(
                image = scaledImage,
                offset = position
            )
        )
    }

    return placements
}

private fun isOverlapping(
    position: Offset,
    width: Int,
    height: Int,
    placements: List<Placement>
): Boolean {
    placements.forEach { existing ->
        val existingWidth = existing.image.width
        val existingHeight = existing.image.height
        val overlapX = position.x < existing.offset.x + existingWidth && position.x + width > existing.offset.x
        val overlapY = position.y < existing.offset.y + existingHeight && position.y + height > existing.offset.y
        if (overlapX && overlapY) return true
    }
    return false
}

private fun ImageBitmap.scale(scale: Float): ImageBitmap {
    val androidBitmap = this.asAndroidBitmap()
    val newWidth = (androidBitmap.width * scale).roundToInt().coerceAtLeast(1)
    val newHeight = (androidBitmap.height * scale).roundToInt().coerceAtLeast(1)
    val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(androidBitmap, newWidth, newHeight, true)
    return scaledBitmap.asImageBitmap()
}

private suspend fun loadMemoImages(context: Context, count: Int): List<ImageBitmap> {
    return withContext(Dispatchers.IO) {
        val images = mutableListOf<ImageBitmap>()
        val loadCount = count.coerceAtMost(MAX_ASSETS)
        repeat(loadCount) { index ->
            val name = "memo${100 + index}.png"
            try {
                context.assets.open(name).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.let { bitmap ->
                        images.add(bitmap.asImageBitmap())
                    }
                }
            } catch (_: Exception) {
            }
        }
        images
    }
}

private const val MIN_IMAGES = 12
private const val MAX_ASSETS = 100