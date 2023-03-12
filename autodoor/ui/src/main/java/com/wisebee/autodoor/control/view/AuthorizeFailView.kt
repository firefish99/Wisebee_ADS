package com.wisebee.autodoor.control.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CancelPresentation
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.theme.view.CircularIcon
import com.wisebee.autodoor.control.R

@Composable
fun AuthorizeFailView(
    modifier: Modifier = Modifier,
    onClick : () -> Unit = { },
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            modifier = Modifier
                .widthIn(max = 460.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularIcon(imageVector = Icons.Default.CancelPresentation)

                Text(
                    text = stringResource(id = R.string.authorize_fail_title),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(id = R.string.authorize_fail_text1),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(id = R.string.authorize_fail_text2),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Button(
            onClick = onClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.action_back))
        }
    }
}

@Preview
@Composable
private fun AuthorizeFailView_Preview() {
    NordicTheme {
        AuthorizeFailView()
    }
}
