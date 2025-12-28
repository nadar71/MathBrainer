package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.components.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.presentation.components.ResultBanner
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model.NumberMarker
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.components.NumberOrderHeader
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun NumberOrderGameScreen(
    initialHighScore: Int = 0,
    onBack: () -> Unit,
    viewModel: NumberOrderViewModel = hiltViewModel()
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
                        text = stringResource(id = R.string.number_order_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(id = R.string.number_order_description),
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
            NumberOrderHeader(state = state)

            val primaryColor = MaterialTheme.colorScheme.primary
            val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
            val onSecondaryColor = MaterialTheme.colorScheme.onSecondary
            
            NumberOrderCanvas(
                itemCount = state.challenge?.itemCount ?: 0,
                isMemorizing = state.isMemorizing,
                challengeKey = state.challenge?.placementSeed ?: state.challengeId,
                revealedCount = state.revealedCount,
                onMarkerTapped = viewModel::onMarkerTapped,
                memorizingColor = primaryColor,
                solvedColor = primaryContainerColor,
                defaultColor = onSecondaryColor
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
                null -> if (state.isMemorizing)
                    ResultBanner(
                        text = stringResource(id = R.string.click_order_instructions),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                else
                    ResultBanner(
                        text = stringResource(id = R.string.click_order_start),
                        color = MaterialTheme.colorScheme.onBackground
                    )
            }

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
private fun NumberOrderCanvas(
    itemCount: Int,
    isMemorizing: Boolean,
    challengeKey: Int,
    revealedCount: Int,
    onMarkerTapped: (Int) -> Unit,
    memorizingColor: Color,
    solvedColor: Color,
    defaultColor: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(320.dp)
        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val markers = remember(challengeKey, canvasSize, itemCount) {
        if (canvasSize == IntSize.Zero || itemCount <= 0) emptyList() else generateMarkerPlacements(
            itemCount = itemCount,
            canvasSize = canvasSize,
            seed = challengeKey
        )
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { canvasSize = it }
            .pointerInput(itemCount, isMemorizing, revealedCount, markers) {
                detectTapGestures { tapOffset ->
                    if (isMemorizing) return@detectTapGestures
                    val tappedMarker = markers.firstOrNull { marker ->
                        val distance = tapOffset.minus(marker.center)
                        distance.getDistance() <= marker.radius
                    }
                    tappedMarker?.let { onMarkerTapped(it.index) }
                }
            }
    ) {
        markers.forEach { marker ->
            val isSolved = marker.index < revealedCount
            val baseColor = when {
                isMemorizing -> memorizingColor
                isSolved -> solvedColor
                else -> defaultColor
            }
            drawCircle(
                color = baseColor,
                radius = marker.radius,
                center = marker.center,
                style = androidx.compose.ui.graphics.drawscope.Fill
            )

            if (isMemorizing || isSolved) {
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = marker.radius
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    val yPos = marker.center.y + (textPaint.textSize / 3)
                    drawText((marker.index).toString(), marker.center.x, yPos, textPaint)
                }
            }
        }
    }
}


private fun generateMarkerPlacements(
    itemCount: Int,
    canvasSize: IntSize,
    seed: Int,
    radius: Float = 42f
): List<NumberMarker> {
    val random = Random(seed)
    val markers = mutableListOf<NumberMarker>()
    val maxWidth = max(canvasSize.width - (radius * 2).roundToInt(), 1)
    val maxHeight = max(canvasSize.height - (radius * 2).roundToInt(), 1)

    repeat(itemCount) { index ->
        var attempts = 0
        var center: Offset
        do {
            val x = random.nextInt(radius.roundToInt(), maxWidth + radius.roundToInt())
            val y = random.nextInt(radius.roundToInt(), maxHeight + radius.roundToInt())
            center = Offset(x.toFloat(), y.toFloat())
            attempts++
        } while (isOverlapping(center, radius, markers) && attempts < 40)

        markers.add(NumberMarker(index = index, center = center, radius = radius))
    }
    return markers
}

private fun isOverlapping(center: Offset, radius: Float, markers: List<NumberMarker>): Boolean {
    markers.forEach { marker ->
        val distance = center.minus(marker.center).getDistance()
        if (distance < radius * 2) return true
    }
    return false
}

/*
private fun Offset.minus(other: Offset): Offset {
    return Offset(x - other.x, y - other.y)
}*/
