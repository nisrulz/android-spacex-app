package com.nisrulz.example.spacexapi.presentation.features.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nisrulz.example.spacexapi.common.contract.utils.EmptyCallback
import com.nisrulz.example.spacexapi.presentation.R
import com.nisrulz.example.spacexapi.presentation.theme.SpacexAPITheme
import com.nisrulz.example.spacexapi.presentation.theme.dimens

@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    @DrawableRes leftNavButtonIcon: Int = -1,
    leftNavButtonAction: EmptyCallback = {},
    @DrawableRes rightNavButtonIcon: Int = -1,
    rightNavButtonAction: EmptyCallback = {}
) = Row(
    modifier = modifier
        .fillMaxWidth()
) {
    Image(
        modifier = Modifier.weight(0.8f, true),
        painter = painterResource(id = R.drawable.logo),
        contentDescription = stringResource(id = R.string.title_main)
    )

    if (leftNavButtonIcon != -1) {
        Image(
            painter = painterResource(id = leftNavButtonIcon),
            contentDescription = "Left Action Button",
            modifier = Modifier
                .weight(0.2f, true)
                .padding(top = MaterialTheme.dimens.large)
                .clickable {
                    leftNavButtonAction()
                }
        )
    }

    if (rightNavButtonIcon != -1) {
        Image(
            painter = painterResource(id = rightNavButtonIcon),
            contentDescription = "Right Action Button",
            modifier = Modifier
                .weight(0.2f, true)
                .padding(top = MaterialTheme.dimens.large)
                .clickable {
                    rightNavButtonAction()
                }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SpacexAPITheme {
        TitleBar(
            leftNavButtonIcon = R.drawable.list_all,
            leftNavButtonAction = {},
            rightNavButtonIcon = R.drawable.back,
            rightNavButtonAction = {}
        )
    }
}
