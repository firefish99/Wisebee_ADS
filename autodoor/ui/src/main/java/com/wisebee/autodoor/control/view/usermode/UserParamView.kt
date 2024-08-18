package com.wisebee.autodoor.control.view.usermode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.WbTheme
import com.wisebee.autodoor.control.view.OpenGapInput
import com.wisebee.autodoor.control.view.ParamInput
import com.wisebee.autodoor.control.view.RefreshButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

@Composable
internal fun UserParamView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nParamCount = 9
    val nValue = remember { mutableStateListOf( *(Array(nParamCount) { 0 }) ) }
    val bPressed = remember { mutableStateListOf( *(Array(nParamCount + 2) { false }) ) }
    var bNewVersion by remember { mutableStateOf(false) }
    if(packet.value[0] == DataToMCU.FID_APP_USER_PARAM) {
        bNewVersion = true
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed.fill(false)
        if(packet.value[1].toInt() >= (2*nParamCount + 3)) {         // BLE-main : 1.2.3 부터
            if (packet.value[2] == DataToMCU.CMD_GET_USER_PARAM) {
                for (i in 0 until nParamCount) {
                    nValue[i] =
                        ByteBuffer.wrap(packet.value, 3 + i * 2, 2)
                            .order(ByteOrder.BIG_ENDIAN).short.toInt()
                }
            }
        }
        else if(packet.value[1].toInt() >= (2*7 + 3)) {    // BLE-main : 1.2.3 이전 버전
            bNewVersion = false
            if (packet.value[2] == DataToMCU.CMD_GET_USER_PARAM) {
                for (i in 0 until 7) {
                    nValue[i] =
                        ByteBuffer.wrap(packet.value, 3 + i * 2, 2)
                            .order(ByteOrder.BIG_ENDIAN).short.toInt()
                }
                nValue[7] = 0
                nValue[8] = 0
            }
        }
    }
    else if(packet.value[0] == DataToMCU.FID_APP_SYS_COMMAND) {
        bPressed[nParamCount + 0] = false
        bPressed[nParamCount + 1] = false
        viewModel.sendCommand(DataToMCU.FID_APP_USER_PARAM, DataToMCU.CMD_GET_USER_PARAM)
    }
    //Timber.tag("UserParamView").e("${nValue[0]}, ${nValue[1]}, ${nValue[2]}")

    fun sendParam(index: Int, value: Int) {
        viewModel.sendCommand(DataToMCU.FID_APP_USER_PARAM,
            byteArrayOf((DataToMCU.CMD_SET_USER_PARAM + index).toByte(),
                *ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(value.toShort()).array()))
    }
    Column (
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(bottom = (10.dp + 16.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "기능설정 변경", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(DataToMCU.FID_APP_USER_PARAM, DataToMCU.CMD_GET_USER_PARAM)
            }
        }

        @Composable
        fun ParamFun(name:String, index:Int, valueRange:ClosedFloatingPointRange<Float>, unit:Int=100) {
            ParamInput(name=name,
                pressed=bPressed[index],
                value=nValue[index],
                onClick={ bPressed[index]=true
                    sendParam(index, nValue[index]) },
                valueRange=valueRange,
                unit=unit)
            {
                nValue[index]=it.roundToInt()
            }
        }
        @Composable
        fun OpenGapFun(name:String, index:Int, valueRange:ClosedFloatingPointRange<Float>, unit:Int=100) {
            OpenGapInput(name=name,
                pressed=bPressed[index],
                value=nValue[index],
                onClick={ bPressed[index]=true
                    sendParam(index, if(nValue[index] < 30) 30 else nValue[index]) },
                valueRange=valueRange,
                unit=unit)
            {
                nValue[index]=it.roundToInt()
            }
        }
        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 16.dp, start = 20.dp, end = 20.dp),

        ) {
            ParamFun(name="열림속도", index=0, valueRange=1000f..2800f)
            ParamFun(name="닫힘속도", index=1, valueRange=500f..2800f)
            OpenGapFun(name="열림 갭", index=2, valueRange=0f..1000f, unit=50)
            ParamFun(name="닫힘 갭", index=3, valueRange=50f..200f, unit=10)
            ParamFun(name="저속 구간 시작", index=4, valueRange=500f..2000f)
            ParamFun(name="저속 구간 속도", index=5, valueRange=300f..1500f)
            ParamFun(name="닫힘 대기 시간", index=6, valueRange=1000f..10000f)
            if(bNewVersion) {
                ParamFun(name="RADAR 감지 최소 거리", index=7, valueRange=10f..2000f, unit=10)
                ParamFun(name="RADAR 감지 최대 거리", index=8, valueRange=10f..2000f, unit=10)
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = WbTheme.getButtonContainer(bPressed[nParamCount + 1]),
                    contentColor = WbTheme.getButtonContent(bPressed[nParamCount + 1])),
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                onClick = {
                    bPressed[nParamCount + 1] = true
                    viewModel.sendCommand(DataToMCU.FID_APP_SYS_COMMAND, DataToMCU.BLE_SAVE_SUBCONFIG)
                },
            ) { Text(text = "사용자 설정 저장", fontSize = 16.sp) }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = WbTheme.getButtonContainer(bPressed[nParamCount]),
                    contentColor = WbTheme.getButtonContent(bPressed[nParamCount])),
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp),
                onClick = {
                    bPressed[nParamCount] = true
                    viewModel.sendCommand(DataToMCU.FID_APP_SYS_COMMAND, DataToMCU.BLE_LOAD_SUBCONFIG)
                },
            ) { Text(text = "사용자 설정 불러오기", fontSize = 16.sp) }

        }
    }
}

@Preview
@Composable
private fun UserParamViewPreview() {
    NordicTheme {
        UserParamView()
    }
}