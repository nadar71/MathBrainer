package eu.indiewalkabout.mathbrainer.feat_credits.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.data.local.Constants.my_website
import eu.indiewalkabout.mathbrainer.core.util.GenericUtil.openUrlInBrowserNotCompose
import eu.indiewalkabout.mathbrainer.feat_credits.presentation.components.CreditsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCreditsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.credits_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CreditsCard(
                title = stringResource(id = R.string.credits_attributions_title),
                description = stringResource(id = R.string.credits_text),
                rightIcon = R.drawable.ic_globe,
                onClick = {
                    openUrlInBrowserNotCompose(context, my_website)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            /*CreditsCard(
                title = stringResource(id = R.string.gdpr_title),
                description = stringResource(id = R.string.gdpr_text)
            )*/
        }
    }
}

