package com.wisebee.autodoor.control.view.adminmode

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.WbTheme
import com.wisebee.autodoor.control.view.RefreshButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun TestModeView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val (focusRequester) = FocusRequester.createRefs()
    //val focusRequester = remember { FocusRequester() }
    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nCommand by remember { mutableStateOf(0) }
    var nResult by remember { mutableStateOf(1) }
    var nProgress by remember { mutableStateOf(0) }
    val bPressed = remember { mutableStateListOf( false, false, false ) }

    val interactionSource = remember { MutableInteractionSource() }
    var sOperMax by remember { mutableStateOf("10") }
    var sOperCount by remember { mutableStateOf("0") }
    var sOperGap by remember { mutableStateOf("5") }
    if(packet.value[0] == DataToMCU.FID_APP_TEST_MODE) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed.fill(false)
        if(packet.value[1].toInt() >= (13 + 2)) {
            nCommand = packet.value[2].toInt()
            val max =
                ByteBuffer.wrap(packet.value, 3, 4).order(ByteOrder.BIG_ENDIAN).int
            sOperGap =
                ByteBuffer.wrap(packet.value, 7, 2).order(ByteOrder.BIG_ENDIAN).short.toString()
            val count =
                ByteBuffer.wrap(packet.value, 9, 4).order(ByteOrder.BIG_ENDIAN).int
            nProgress = if(max == 0) 0 else 100 * count / max
            nResult = packet.value[13].toInt() + 2
            sOperMax = max.toString()
            sOperCount = count.toString()
        }
    }
    //Timber.tag("ModeChangeView").e("$nMode")

    Column (
        //verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(bottom = 10.dp + 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "테스트 모드", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    //.align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(DataToMCU.FID_APP_TEST_MODE, DataToMCU.CMD_GET_TEST_MODE)
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column (
                modifier = Modifier.padding(end = 30.dp),
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "동작 횟수 : ", fontSize = 16.sp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(end = 10.dp)
                    )
                    BasicTextField(
                        value = sOperMax,
                        onValueChange = { if (it.isEmpty() || it.toInt() in 1..300000) sOperMax = it },
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black, textAlign = TextAlign.End),
                        modifier = Modifier
                            .height(30.dp).width(80.dp)
                            .focusRequester(focusRequester)
                            .onKeyEvent {
                                if (it.key == Key.Enter)
                                    focusRequester.requestFocus()
                                false
                            },

                        interactionSource = interactionSource,
                        enabled = true,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                    ) {
                        TextFieldDefaults.TextFieldDecorationBox(
                            value = sOperMax,
                            innerTextField = it,
                            singleLine = true,
                            enabled = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                                top = 5.dp, bottom = 5.dp, start = 0.dp, end = 5.dp
                            )
                        )
                    }
                    Text(
                        text = "회", fontSize = 16.sp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(start = 10.dp, end = 10.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "동작 간격 : ", fontSize = 16.sp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(end = 10.dp)
                    )
                    BasicTextField(
                        value = sOperGap,
                        onValueChange = { if (it.isEmpty() || it.toInt() in 1..180) sOperGap = it },
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black, textAlign = TextAlign.End),
                        modifier = Modifier
                            .height(30.dp).width(80.dp)
                            .focusRequester(focusRequester)
                            .onKeyEvent {
                                if (it.key == Key.Enter)
                                    focusRequester.requestFocus()
                                false
                            },

                        interactionSource = interactionSource,
                        enabled = true,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                    ) {
                        TextFieldDefaults.TextFieldDecorationBox(
                            value = sOperGap,
                            innerTextField = it,
                            singleLine = true,
                            enabled = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                                top = 5.dp, bottom = 5.dp, start = 0.dp, end = 5.dp
                            )
                        )
                    }
                    Text(
                        text = "초", fontSize = 16.sp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(start = 10.dp, end = 10.dp)
                    )
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = WbTheme.getButtonContainer(bPressed[0]),
                    contentColor = WbTheme.getButtonContent(bPressed[0])),
                enabled = true,
                modifier = Modifier
                    .width(120.dp)
                    .height(50.dp),
                onClick = {
                    if(sOperMax.isNotEmpty() && sOperGap.isNotEmpty()) {
                        bPressed[0] = true
                        viewModel.sendCommand(
                            DataToMCU.FID_APP_TEST_MODE,
                            byteArrayOf(
                                DataToMCU.CMD_SET_TEST_MODE,
                                *ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
                                    .putInt(sOperMax.toInt()).array(),
                                *ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
                                    .putShort(sOperGap.toShort()).array()
                            )
                        )
                    }
                },
            ) { Text(text = "설정", fontSize = 16.sp) }
        }

        Text(
            text = when(nResult) {
                1 -> "테스트모드가 정상적으로 완료 되었습니다."
                DataToMCU.TMODE_ERR_NONE -> {
                    when (nCommand) {
                        DataToMCU.CMD_START_TEST_MODE.toInt() -> "테스트모드가 시작 되었습니다."
                        DataToMCU.CMD_STOP_TEST_MODE.toInt() -> "테스트모드가 명령에 의해 종료 되었습니다."
                        DataToMCU.CMD_CRASH_TEST_MODE.toInt() -> "테스트모드가 다른 동작에 의해 중지 되었습니다."
                        DataToMCU.CMD_SET_TEST_MODE.toInt() -> "동작횟수 및 동작간격이 설정 되었습니다."
                        else -> ""
                    }
                }
                DataToMCU.TMODE_ERR_STAT -> "테스트모드를 시작할 수 없습니다.(컨트롤러가 Run상태가 아님)"
                DataToMCU.TMODE_ERR_ERROR -> "테스트모드를 시작할 수 없습니다.(센서 에러)"
                DataToMCU.TMODE_ERR_TMODE -> "테스트모드를 시작할 수 없습니다.(다른 테스트 모드 진행중)"
                DataToMCU.TMODE_OK_ING -> "테스트모드가 진행중입니다."
                DataToMCU.TMODE_OK_DONE -> "테스트모드가 정상적으로 완료 되었습니다."
                else -> ""
            },
            fontSize = 16.sp,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
                .padding(top = 10.dp + 16.dp, start = 20.dp, end = 20.dp)
        )

        Text(
            text = "$sOperCount / $sOperMax",
            fontSize = 16.sp,
            modifier = Modifier
                .align(alignment = Alignment.End)
                .wrapContentWidth()
                .padding(top = 10.dp + 16.dp, start = 20.dp, end = 20.dp)
        )
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp),
                trackColor = Color.LightGray,
                color = Color(0xff00a0a0),
                progress = nProgress / 100f
            )
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = WbTheme.getButtonContainer(bPressed[1]),
                contentColor = WbTheme.getButtonContent(bPressed[1])),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp),
            onClick = {
                bPressed[1] = true
                viewModel.sendCommand(DataToMCU.FID_APP_TEST_MODE, DataToMCU.CMD_START_TEST_MODE)
            },
        ) { Text(text = "테스트모드 시작", fontSize = 16.sp) }
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = WbTheme.getButtonContainer(bPressed[2]),
                contentColor = WbTheme.getButtonContent(bPressed[2])),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            onClick = {
                bPressed[2] = true
                viewModel.sendCommand(DataToMCU.FID_APP_TEST_MODE, DataToMCU.CMD_STOP_TEST_MODE)
            },
        ) { Text(text = "테스트모드 종료", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun TestModeViewPreview() {
    NordicTheme {
        TestModeView()
    }
}