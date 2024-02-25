package com.nisrulz.example.spacexapi.presentation.features.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisrulz.example.spacexapi.common.contract.utils.EmptyCallback
import com.nisrulz.example.spacexapi.presentation.R
import com.nisrulz.example.spacexapi.presentation.theme.SpacexAPITheme

@Composable
fun EmptyComponent(
    modifier: Modifier = Modifier,
    message: String,
    navigateBack: EmptyCallback = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val textSize = 25.sp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.empty),
                contentDescription = "Empty State"
            )
            Text(
                color = MaterialTheme.colorScheme.background,
                fontSize = textSize,
                textAlign = TextAlign.Center,
                text = message
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                modifier = Modifier.clickable {
                    navigateBack()
                },
                color = MaterialTheme.colorScheme.background,
                textDecoration = TextDecoration.Underline,
                fontSize = textSize,
                textAlign = TextAlign.Center,
                text = "Go Back"
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SpacexAPITheme {
        EmptyComponent(message = "Error String")
    }
}
