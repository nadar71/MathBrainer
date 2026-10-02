package eu.indiewalkabout.mathbrainer.feat_home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameUiModel

@Composable
fun GameGrid(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    games: List<GameUiModel>,
    onGameSelected: (GameUiModel) -> Unit,
    showMinimal: Boolean = false
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(games) { game ->
            GameCard(
                gameUiModel = game,
                onGameSelected = onGameSelected,
                showMinimal = showMinimal
            )
        }
    }
}
