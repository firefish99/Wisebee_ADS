package com.wisebee.autodoor.control.view.adminmode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.WbTheme
import com.wisebee.autodoor.control.view.ParamInput
import com.wisebee.autodoor.control.view.RefreshButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

@Composable
internal fun TestModeView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nCommand by remember { mutableStateOf(0) }
    var nParamValue by remember { mutableStateOf(10) }
    var nResult by remember { mutableStateOf(0) }
    var nProgress by remember { mutableStateOf(0) }
    val bPressed = remember { mutableStateListOf( false, false, false ) }
    if(packet.value[0] == DataToMCU.FID_APP_TEST_MODE) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed.fill(false)
        if(packet.value[1].toInt() >= (7 + 2)) {
            nCommand = packet.value[2].toInt()
            nParamValue =
                ByteBuffer.wrap(packet.value, 3, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
            nProgress =
                ByteBuffer.wrap(packet.value, 5, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
            nResult = packet.value[7].toInt() + 2
        }
    }
    //Timber.tag("ModeChangeView").e("$nMode")

    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(bottom = 10.dp),
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

        ParamInput(
            modifier = Modifier.padding(top = 0.dp, bottom = 16.dp, start = 20.dp, end = 20.dp),
            name = "동작시간",
            button = "설정",
            pressed = bPressed[0],
            value = nParamValue,
            onClick = {
                bPressed[0] = true
                viewModel.sendCommand(DataToMCU.FID_APP_TEST_MODE,
                    byteArrayOf(DataToMCU.CMD_SET_TEST_MODE,
                        *ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(nParamValue.toShort()).array()))
            },
            valueRange = 10f..600f,
            unit = 10,
        ) { nParamValue = it.roundToInt() }

        Text(
            text = when(nResult) {
                1 -> "테스트모드가 정상적으로 완료 되었습니다."
                DataToMCU.TMODE_ERR_NONE -> {
                    when (nCommand) {
                        DataToMCU.CMD_START_TEST_MODE.toInt() -> "테스트모드가 시작 되었습니다."
                        DataToMCU.CMD_STOP_TEST_MODE.toInt() -> "테스트모드가 명령에 의해 종료 되었습니다."
                        DataToMCU.CMD_CRASH_TEST_MODE.toInt() -> "테스트모드가 다른 동작에 의해 중지 되었습니다."
                        DataToMCU.CMD_SET_TEST_MODE.toInt() -> "동작시간이 설정 되었습니다."
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
                .padding(top = 10.dp, start = 20.dp, end = 20.dp)
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
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
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
                .padding(horizontal = 20.dp),
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