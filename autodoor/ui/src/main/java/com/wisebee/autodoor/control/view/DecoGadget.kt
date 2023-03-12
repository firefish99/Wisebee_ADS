package com.wisebee.autodoor.control.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wisebee.autodoor.control.R
import com.wisebee.autodoor.control.WbTheme
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun RefreshButton() {
    Image(
        painter = painterResource(id = R.drawable.ic_refresh),
        contentDescription = "다시 불러오기",
        modifier = Modifier
            .padding(4.dp)
            .width(30.dp)
            .height(30.dp)
            .border(width = 1.dp, color = Color(0xff00a9ce))
    )
}

@Composable
internal fun ParamInput(
    modifier: Modifier = Modifier,
    name: String = "",
    value: Int = 0,
    button: String = "",
    onClick : () -> Unit = { },
    onValueChange : (Float) -> Unit = { },
    valueRange :  ClosedFloatingPointRange<Float> = 0f..10f,
    steps : Int = 0,
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-15).dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${name}${value}", fontSize = 18.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
            Box(
                modifier = Modifier
                    .background(color = WbTheme.setButtonContainer,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .wrapContentWidth()
                    //.wrapContentHeight()
                    .height(30.dp)
                    .weight(1f, false)
                    .clickable { onClick() },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = button,
                    fontSize = 16.sp,
                    color = WbTheme.setButtonContent,
                )
            }
        }

        val interactionSource = remember { MutableInteractionSource() }
        val dragged = interactionSource.collectIsDraggedAsState()

        Slider(
            value = value.toFloat(),
            onValueChange = {
                if(dragged.value)
                    onValueChange(it)
            },
            valueRange = valueRange,
            steps = steps,
            interactionSource = interactionSource,
        )
    }
}

@Preview
@Composable
private fun RefreshButtonPreview() {
    NordicTheme {
        RefreshButton()
    }
}

@Preview
@Composable
private fun ParamInputPreview() {
    NordicTheme {
        ParamInput(name = "Test : ", button = "Send", steps = 10)
    }
}