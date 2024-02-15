package com.nisrulz.example.spacexapi.presentation.features.listoflaunches

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.presentation.R
import com.nisrulz.example.spacexapi.presentation.theme.SpacexAPITheme
import com.nisrulz.example.spacexapi.presentation.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchInfoItem(
    launchInfo: LaunchInfo,
    onBookmark: (LaunchInfo) -> Unit,
    onClick: (String) -> Unit
) {
    ElevatedCard(
        modifier =
        Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(MaterialTheme.dimens.small),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = {
            onClick(launchInfo.id)
        }
    ) {
        Row {
            AsyncImage(
                model =
                ImageRequest.Builder(LocalContext.current)
                    .data(launchInfo.logo)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder),
                contentDescription = stringResource(R.string.logo_description),
                contentScale = ContentScale.Crop,
                modifier =
                Modifier
                    .padding(MaterialTheme.dimens.medium)
                    .fillMaxWidth(0.4f)
            )

            Column(
                modifier =
                Modifier
                    .padding(MaterialTheme.dimens.medium)
                    .fillMaxSize()
            ) {
                Text(
                    text = "LAUNCH ${launchInfo.flight_number}",
                    style =
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = launchInfo.name,
                    style =
                    TextStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(text = launchInfo.getFormattedDate())

                Spacer(modifier = Modifier.fillMaxHeight(0.7f))

                val drawableIdForBookmark =
                    if (launchInfo.isBookmarked) {
                        R.drawable.bookmark
                    } else {
                        R.drawable.not_bookmarked
                    }
                Image(
                    painter = painterResource(id = drawableIdForBookmark),
                    contentDescription = stringResource(id = R.string.bookmark),
                    modifier =
                    Modifier
                        .align(Alignment.End)
                        .clickable {
                            onBookmark(launchInfo)
                        }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SpacexAPITheme {
        val testLaunchInfo =
            LaunchInfo(
                date_local = "",
                details = "",
                flight_number = 1,
                id = "1",
                logo = "",
                name = "Name 1",
                success = false,
                isBookmarked = false
            )
        LaunchInfoItem(
            launchInfo = testLaunchInfo,
            onClick = {},
            onBookmark = { }
        )
    }
}
