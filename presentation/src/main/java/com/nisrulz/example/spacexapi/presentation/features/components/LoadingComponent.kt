package com.nisrulz.example.spacexapi.presentation.features.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nisrulz.example.spacexapi.presentation.theme.SpacexAPITheme
import com.nisrulz.example.spacexapi.presentation.theme.dimens

@Composable
fun LoadingComponent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.dimens.small),
    ) {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .align(Alignment.Center),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SpacexAPITheme {
        LoadingComponent()
    }
}
