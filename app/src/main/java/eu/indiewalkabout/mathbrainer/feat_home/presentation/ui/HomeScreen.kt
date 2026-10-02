package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.indiewalkabout.mathbrainer.core.presentation.navigation.AppRoute
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.navigation.GameRouteResolver
import eu.indiewalkabout.mathbrainer.core.presentation.navigation.HomeRoute
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_home.presentation.components.GameGrid
import eu.indiewalkabout.mathbrainer.feat_home.presentation.components.LoadingContent

@Composable
fun HomeScreen(
    onStatisticsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onGameSelected: (AppRoute) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val state by homeViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_chart),
                    contentDescription = "Statistics",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .clickable(onClick = onStatisticsClick),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .clickable(onClick = onSettingsClick),
                    contentScale = ContentScale.Fit
                )
            }
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.math_brainer_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
            when {
                state.isLoading -> {
                    LoadingContent(padding)
                }

                !state.error.isNullOrEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Error loading scores: ${state.error}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { homeViewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    GameGrid(
                        modifier = Modifier.padding(top = 16.dp),
                        paddingValues = padding,
                        games = state.games,
                        showMinimal = true,
                        onGameSelected = { game ->
                            onGameSelected(game.definition.id.toRoute())
                        },
                    )
                }

            }
        }
    }
}

private fun String.toRoute(): AppRoute {
    return GameTypes.fromId(this)
        ?.let(GameRouteResolver::resolve)
        ?: HomeRoute
}
