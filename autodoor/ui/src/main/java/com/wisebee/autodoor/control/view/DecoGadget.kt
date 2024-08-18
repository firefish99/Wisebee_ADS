package com.wisebee.autodoor.control.view

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wisebee.autodoor.control.R
import com.wisebee.autodoor.control.WbTheme
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun RefreshButton(onClick : () -> Unit = { }) {
    Box( modifier = Modifier .clickable { onClick() } ) {
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
}

@Composable
internal fun StartButton(
    modifier: Modifier = Modifier,
    button: String = "",
    pressed: Boolean = false,
    onClick : () -> Unit = { },
) {
    Box(
        modifier = modifier
            .background(
                color = WbTheme.getButtonContainer(pressed),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .wrapContentWidth()
            //.wrapContentHeight()
            .height(30.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = button,
            fontSize = 16.sp,
            color = WbTheme.getButtonContent(pressed),
        )
    }
}

@Composable
internal fun SmallButton(
    modifier: Modifier = Modifier,
    button: String = "",
    pressed: Boolean = false,
    onClick : () -> Unit = { },
) {
    Box(
        modifier = modifier
            .background(
                color = WbTheme.getButtonContainer(pressed),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp)
            .wrapContentWidth()
            //.wrapContentHeight()
            .height(22.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = button,
            fontSize = 14.sp,
            color = WbTheme.getButtonContent(pressed),
        )
    }
}

@Composable
internal fun ParamInput(
    modifier: Modifier = Modifier,
    name: String = "",
    value: Int = 0,
    button: String = "전 송",
    pressed: Boolean = false,
    onClick : () -> Unit = { },
    valueRange :  ClosedFloatingPointRange<Float> = 0f..10f,
    unit : Int = 1,
    steps : Int = -1,
    onValueChange : (Float) -> Unit = { },
) {
    val lsteps = if(unit == 1) 0
                else if(steps != -1) steps
                else ((valueRange.endInclusive - valueRange.start) / unit).toInt() - 1

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-15).dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$name : $value", fontSize = 18.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
            Box(
                modifier = Modifier
                    .background(
                        color = WbTheme.getButtonContainer(pressed),
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
                    color = WbTheme.getButtonContent(pressed),
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
            steps = lsteps,
            interactionSource = interactionSource,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SimpleParamInput(
    modifier: Modifier = Modifier,
    name: String = "",
    enabled: Boolean = true,
    value: Int = 0,
    valueRange :  ClosedFloatingPointRange<Float> = 0f..10f,
    unit : Int = 1,
    steps : Int = -1,
    onValueChange : (Float) -> Unit = { },
) {
    val lsteps = if(unit == 1) 0
                else if(steps != -1) steps
                else ((valueRange.endInclusive - valueRange.start) / unit.toFloat()).toInt() - 1

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-22).dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$name : $value", fontSize = 16.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        val dragged = interactionSource.collectIsDraggedAsState()

        Slider(
            enabled = enabled,
            value = value.toFloat(),
            onValueChange = {
                if(dragged.value)
                    onValueChange(it)
            },
            valueRange = valueRange,
            steps = lsteps,
            interactionSource = interactionSource,
            thumb = remember(interactionSource) { {
                SliderDefaults.Thumb(
                    modifier = Modifier.padding(top = 3.dp),
                    interactionSource = interactionSource,
                    colors = SliderDefaults.colors(),
                    thumbSize = DpSize(15.dp, 15.dp),
                    enabled = true
                )
            } },
        )
    }
}

@Composable
internal fun OpenGapInput(
    modifier: Modifier = Modifier,
    name: String = "",
    value: Int = 0,
    button: String = "전 송",
    pressed: Boolean = false,
    onClick : () -> Unit = { },
    valueRange :  ClosedFloatingPointRange<Float> = 0f..10f,
    unit : Int = 1,
    steps : Int = -1,
    onValueChange : (Float) -> Unit = { },
) {
    val lsteps = if(unit == 1) 0
    else if(steps != -1) steps
    else ((valueRange.endInclusive - valueRange.start) / unit).toInt() - 1

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-15).dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$name : ${if(value < 30) 30 else value}", fontSize = 18.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
            if(value < 150)
                Text(
                    text = "수동문열림유지 off", fontSize = 16.sp,
                    color = Color(0x80,0x20,0x20),
                    modifier = Modifier
                        .wrapContentWidth()
                )

            Box(
                modifier = Modifier
                    .background(
                        color = WbTheme.getButtonContainer(pressed),
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
                    color = WbTheme.getButtonContent(pressed),
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
            steps = lsteps,
            interactionSource = interactionSource,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SimpleOpenGapInput(
    modifier: Modifier = Modifier,
    name: String = "",
    value: Int = 0,
    valueRange :  ClosedFloatingPointRange<Float> = 0f..10f,
    unit : Int = 1,
    steps : Int = -1,
    onValueChange : (Float) -> Unit = { },
) {
    val lsteps = if(unit == 1) 0
    else if(steps != -1) steps
    else ((valueRange.endInclusive - valueRange.start) / unit.toFloat()).toInt() - 1

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-22).dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$name : ${if (value < 30) 30 else value}", fontSize = 16.sp,
                    modifier = Modifier
                        .wrapContentWidth()
                )
                if (value < 150)
                    Text(
                        text = "수동문열림유지 off", fontSize = 16.sp,
                        color = Color(0x80, 0x20, 0x20),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(start=30.dp)
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
            steps = lsteps,
            interactionSource = interactionSource,
            thumb = remember(interactionSource) { {
                SliderDefaults.Thumb(
                    modifier = Modifier.padding(top = 3.dp),
                    interactionSource = interactionSource,
                    colors = SliderDefaults.colors(),
                    thumbSize = DpSize(15.dp, 15.dp),
                    enabled = true
                )
            } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChirpParamInput(
    modifier: Modifier = Modifier,
    name: String = "램프 지속 시간",
    value: Int = 220,
    valueRange :  ClosedFloatingPointRange<Float> = 220f..4400f,
    steps : Int = 1,
    onValueChange : (Float) -> Unit = { },
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-22).dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$name : $value",// + if(value == 2310) 1100 else value,
                fontSize = 16.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        val dragged = interactionSource.collectIsDraggedAsState()

        Slider(
            value = (if(value == 1100) 2310 else value).toFloat(),
            onValueChange = {
                if(dragged.value)
                    onValueChange(it)
            },
            valueRange = valueRange,
            steps = steps,
            interactionSource = interactionSource,
            thumb = remember(interactionSource) { {
                SliderDefaults.Thumb(
                    modifier = Modifier.padding(top = 3.dp),
                    interactionSource = interactionSource,
                    colors = SliderDefaults.colors(),
                    thumbSize = DpSize(15.dp, 15.dp),
                    enabled = true
                )
            } },
        )
    }
}

@Composable
internal fun ParamSwitch(
    modifier: Modifier = Modifier,
    name: String = "",
    value: Boolean = false,
    onCheckedChange : (Boolean) -> Unit = { },
) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name, fontSize = 16.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
            Switch(checked = value, onCheckedChange = onCheckedChange)
        }
}

@Composable
internal fun SimpleParamDisplay(
    modifier: Modifier = Modifier,
    name: String = "",
    value: String = "",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name, fontSize = 16.sp,
            modifier = Modifier
                .wrapContentWidth()
        )
        Text(
            text = value, fontSize = 16.sp,
            modifier = Modifier
                .wrapContentWidth()
        )
    }
}

/*
@Preview
@Composable
private fun RefreshButtonPreview() {
    NordicTheme {
        RefreshButton()
    }
}

@Preview
@Composable
private fun StartButtonPreview() {
    NordicTheme {
        StartButton(button = "시작")
    }
}

@Preview
@Composable
private fun ParamInputPreview() {
    NordicTheme {
        ParamInput(name = "Test", steps = 10)
    }
}
 */

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SimpleParamInputPreview() {
    val nParamValue = remember {
        mutableStateOf( 1100 )
    }

    NordicTheme {
        Column {
            RefreshButton()
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            StartButton(button = "시작")
            Spacer(modifier = Modifier.padding(vertical = 2.dp))
            SmallButton(button = "Enable")
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            ParamInput(name = "문닫힘대기 시간", value = 1000, unit = 200, valueRange = 0f..2000f)
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            SimpleParamInput(name = "문닫힘대기 시간", value = 500, unit = 100, valueRange = 100f..2000f)
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            OpenGapInput(name = "열림 갭", value = 100, unit = 50, valueRange = 0f..1000f)
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            SimpleOpenGapInput(name = "열림 갭", value = 100, unit = 50, valueRange = 0f..1000f)
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            ParamSwitch(name = "워치독 활성화", value = true)
            ParamSwitch(name = "워치독 활성화", value = false)
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            SimpleParamDisplay(name = "측정 거리 값", value="1000")
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            ChirpParamInput(value = nParamValue.value) {
                val value = it.toInt()
                nParamValue.value = if(value == 2310) 1100 else value
            }

            val singleLine = true
            val enabled = true
            var text : String = "123"
            val colors = TextFieldDefaults.textFieldColors()
            val interactionSource = remember { MutableInteractionSource() }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(fontSize = 16.sp, textAlign = TextAlign.End),
                modifier = Modifier
                    //.align(alignment = Alignment.End)
                    .height(25.dp),

                interactionSource = interactionSource,
                enabled = true,
                singleLine = true
            ) {
                TextFieldDefaults.TextFieldDecorationBox(
                    value = text,
                    innerTextField = it,
                    singleLine = true,
                    enabled = true,
                    visualTransformation = VisualTransformation.None,
                    trailingIcon = { /* ... */ },
                    placeholder = {
                        Text(
                            text = "횟수",
                            fontSize = 14.sp,
                        )
                    },
                    interactionSource = interactionSource,
                    // keep horizontal paddings but change the vertical
                    contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                        top = 0.dp, bottom = 0.dp
                    )
                )
            }
        }
    }
}
