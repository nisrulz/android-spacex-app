package com.nisrulz.example.spacexapi.presentation.features.launchdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nisrulz.example.spacexapi.common.contract.utils.EmptyCallback
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.presentation.R
import com.nisrulz.example.spacexapi.presentation.features.components.TitleBar
import com.nisrulz.example.spacexapi.presentation.theme.SpacexAPITheme

@Composable
fun LaunchDetailSuccessComponent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    state: LaunchDetailViewModel.UiState,
    navigateBack: EmptyCallback = {}
) {
    Box(
        modifier =
        modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        state.data?.apply {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                TitleBar(
                    rightNavButtonIcon = R.drawable.back,
                    rightNavButtonAction = {
                        navigateBack()
                    }
                )

                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    AsyncImage(
                        model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(logo)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.placeholder),
                        contentDescription = stringResource(R.string.logo_description),
                        contentScale = ContentScale.Crop,
                        modifier =
                        Modifier
                            .fillMaxHeight(0.5f)
                            .padding(48.dp)
                            .fillMaxWidth()
                    )

                    Text(
                        text = "LAUNCH $flight_number",
                        style =
                        TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = name,
                        style =
                        TextStyle(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = Modifier.padding(16.dp))

                    Text(
                        text = "Date: ${getFormattedDate()}",
                        style =
                        TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(
                        text = "Details",
                        style =
                        TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = getDetailsString(),
                        style =
                        TextStyle(
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.padding(16.dp))

                    Text(
                        text = "Was Successful: ${wasSuccessfulString()}",
                        style =
                        TextStyle(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Wire in Snackbar Widget
        SnackbarHost(
            hostState = snackbarHostState,
            modifier =
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SpacexAPITheme {
        LaunchDetailSuccessComponent(
            state =
            LaunchDetailViewModel.UiState(
                data =
                LaunchInfo(
                    date_local = "2023-1-19",
                    details = "Some Details",
                    flight_number = 1,
                    id = "1",
                    logo = "",
                    name = "Flight Preview Name",
                    success = false,
                    isBookmarked = false
                )
            ),
            snackbarHostState = SnackbarHostState()
        )
    }
}
