package com.wisebee.autodoor.control.view.adminparam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
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
import com.wisebee.autodoor.control.view.RefreshButton
import com.wisebee.autodoor.control.view.SmallButton
import com.wisebee.autodoor.control.view.StartButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

@Composable
internal fun ControllerBLEView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nResult by remember { mutableStateOf(0) }
    var nDispRes by remember { mutableStateOf(0) }
    val macAddress1 = remember { mutableStateListOf<Byte>( 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 ) }
    val macAddress2 = remember { mutableStateListOf<Byte>( 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 ) }
    var bNewVersion by remember { mutableStateOf(false) }
    val battery = remember { mutableStateListOf( 3300, 3300 ) }
    val bEnabled = remember { mutableStateListOf( true, false ) }
    val bIRFunction = remember { mutableStateListOf( false, false ) }                // function view/hide, enable/disable
    val bPressed = remember { mutableStateListOf( false, false, false, false, false ) }

    if(packet.value[0] == DataToMCU.FID_APP_BLE_COMMAND && packet.value[1].toInt() >= 3) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed.fill(false)

        when(packet.value[2]) {
            DataToMCU.CMD_GET_BLE_STATUS -> {
                if(packet.value[1].toInt() >= ((1 + 2 + 6) * 2 + 2 + 1)) {      // BLE-main : 1.2.1 부터
                    bNewVersion = true
                    bEnabled[0] = packet.value[11] == 1.toByte()
                    bEnabled[1] = packet.value[20] == 1.toByte()
                    for (i in 0 until 6) {
                        macAddress1[i] = packet.value[3 + i]
                        macAddress2[i] = packet.value[12 + i]
                    }
                    battery[0] = ByteBuffer.wrap(packet.value, 9, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                    battery[0] = battery[0].and(0x7fff)
                    battery[1] = ByteBuffer.wrap(packet.value, 18, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                    battery[1] = battery[1].and(0x7fff)
                }
                else if(packet.value[1].toInt() >= (2 + 6 + 2 + 1)) {           // BLE-main : 1.2.1 이전 버전
                    bNewVersion = false
                    bEnabled[0] = true
                    bEnabled[1] = false
                    for (i in 0 until 6)
                        macAddress1[i] = packet.value[3 + i]
                    battery[0] = ByteBuffer.wrap(packet.value, 9, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                    battery[0] = battery[0].and(0x7fff)
                    battery[1] = 0
                }

                if(packet.value[1].toInt() >= ((1 + 2 + 6 + 1) * 2 + 2 + 1)) {          // BLE-main : 1.2.3 부터
                    bIRFunction[0] = true
                    bIRFunction[1] = packet.value[21] == 1.toByte() || packet.value[22] == 1.toByte()
                }
                else {                                                                  // BLE-main : 1.2.3 이전 버전
                    bIRFunction[0] = false
                    bIRFunction[1] = false
                }

                /*
                if(packet.value[1].toInt() >= (2 + 6 + 2 + 1)) {
                    for (i in 0 until 6)
                        macAddress1[i] = packet.value[3 + i]
                    battery1 = ByteBuffer.wrap(packet.value, 9, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                    battery1 = battery1.and(0x7fff)
                }*/
                nDispRes = nResult
                nResult  = 0
            }
            DataToMCU.CMD_CLEAR_BUTTON_MAC -> {
                nResult  = packet.value[3].toInt()
                nDispRes = nResult
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_GET_BLE_STATUS)
            }
            DataToMCU.CMD_RESET_BLE_MODULE -> {
                nResult  = packet.value[3].toInt()
                nDispRes = nResult
            }
            DataToMCU.CMD_ENABLE_BLE_BUTTON -> {
                nResult  = packet.value[3].toInt()
                nDispRes = nResult
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_GET_BLE_STATUS)
            }
            DataToMCU.CMD_ENABLE_IR_BUTTON -> {
                nResult  = packet.value[3].toInt()
                nDispRes = nResult
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_GET_BLE_STATUS)
            }
        }
    }
    val nPercent1 = if (battery[0] < 0) 0
            else if(battery[0] > 3000) 100
            else ((battery[0] - 0f) * 100f / (3000f - 0f)).roundToInt()
    val nPercent2 = if (battery[1] < 0) 0
            else if(battery[1] > 3000) 100
            else ((battery[1] - 0f) * 100f / (3000f - 0f)).roundToInt()

    //Timber.tag("ControllerBLEView").e("${nParamValue[0]}, ${nParamValue[1]}, ${nParamValue[2]}")

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(bottom = (10.dp + 16.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "보드,버튼 BLE 설정", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_GET_BLE_STATUS)
            }
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(start = 10.dp, end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .padding(bottom = 8.dp)

            ) {
                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "BLE 버튼 #1", fontSize = 18.sp,
                        color = if(!bEnabled[0]) Color.Gray else Color.Unspecified,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(end = 10.dp)
                    )
                    if(bNewVersion) {
                        SmallButton(
                            modifier = Modifier
                                .width(80.dp),
                            pressed = bPressed[2],
                            button = if (bEnabled[0]) "Disable" else "Enable"
                        ) {
                            bPressed[2] = true
                            viewModel.sendCommand(
                                DataToMCU.FID_APP_BLE_COMMAND,
                                byteArrayOf(
                                    DataToMCU.CMD_ENABLE_BLE_BUTTON,
                                    0.toByte(),
                                    (if (bEnabled[0]) 0 else 1).toByte()
                                )
                            )
                        }
                    }
                }
                Text(
                    text = "배터리 : ${nPercent1}%", fontSize = 18.sp,
                    color = if(!bEnabled[0]) Color.Gray else if (battery[0] < 1000) Color.Red else Color.Unspecified,
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .wrapContentWidth(),
                )
                Text(
                    text = "MAC : " + "%02X:%02X:%02X:%02X:%02X:%02X".format(macAddress1[0], macAddress1[1], macAddress1[2], macAddress1[3], macAddress1[4], macAddress1[5]),
                    fontSize = 18.sp,
                    color = if(!bEnabled[0]) Color.Gray else Color.Unspecified,
                    modifier = Modifier
                        .wrapContentWidth()
                )
            }
            Divider (
                color = Color.Black,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
            )

            if(bNewVersion) {
                Column(
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .align(alignment = Alignment.Start)
                            .wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            text = "BLE 버튼 #2", fontSize = 18.sp,
                            color = if(!bEnabled[1]) Color.Gray else Color.Unspecified,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(end = 10.dp)
                        )
                        SmallButton(
                            modifier = Modifier
                                .width(80.dp),
                            pressed = bPressed[3],
                            button = if(bEnabled[1]) "Disable" else "Enable"
                        ) {
                            bPressed[3] = true
                            viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND,
                                byteArrayOf(DataToMCU.CMD_ENABLE_BLE_BUTTON, 1.toByte(), (if(bEnabled[1]) 0 else 1).toByte()))
                        }
                    }
                    Text(
                        text = "배터리 : ${nPercent2}%", fontSize = 18.sp,
                        color = if(!bEnabled[1]) Color.Gray else if (battery[1] < 1000) Color.Red else Color.Unspecified,
                        modifier = Modifier
                            .align(alignment = Alignment.Start)
                            .wrapContentWidth(),
                    )
                    Text(
                        text = "MAC : " + "%02X:%02X:%02X:%02X:%02X:%02X".format(macAddress2[0], macAddress2[1], macAddress2[2], macAddress2[3], macAddress2[4], macAddress2[5]),
                        fontSize = 18.sp,
                        color = if(!bEnabled[1]) Color.Gray else Color.Unspecified,
                        modifier = Modifier
                            .wrapContentWidth()
                    )
                }
                Divider (
                    color = Color.Black,
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )
            }

            if(bIRFunction[0]) {
                Column(
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .align(alignment = Alignment.Start)
                            .wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            text = "IR 버튼", fontSize = 18.sp,
                            color = if(!bIRFunction[1]) Color.Gray else Color.Unspecified,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(end = 10.dp)
                        )
                        SmallButton(
                            modifier = Modifier
                                .width(80.dp),
                            pressed = bPressed[4],
                            button = if(bIRFunction[1]) "Disable" else "Enable"
                        ) {
                            bPressed[4] = true
                            viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND,
                                (if(bIRFunction[1]) byteArrayOf(DataToMCU.CMD_ENABLE_IR_BUTTON,0,0) else byteArrayOf(DataToMCU.CMD_ENABLE_IR_BUTTON,1,1)) )
                        }
                    }
                }
                Divider (
                    color = Color.Black,
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                StartButton(
                    modifier = Modifier
                        .width(150.dp)
                        .padding(end = 20.dp),
                    pressed = bPressed[1],
                    button = "BLE 모듈 리셋"
                ) {
                    bPressed[1] = true
                    viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_RESET_BLE_MODULE)
                }
                StartButton(
                    modifier = Modifier
                        .defaultMinSize(130.dp),
                    pressed = bPressed[0],
                    button = "MAC 초기화"
                ) {
                    bPressed[0] = true
                    viewModel.sendCommand(
                        DataToMCU.FID_APP_BLE_COMMAND,
                        DataToMCU.CMD_CLEAR_BUTTON_MAC
                    )
                }
            }
        }
        Text(
            text = when(nDispRes) {
                1 -> "BLE 버튼 MAC 주소가 초기화 되었습니다."
                2 -> "BLE 보드가 리셋됩니다. 재 연결 하시기 바랍니다."
                3 -> "BLE 버튼이 비활성화 되었습니다."
                4 -> "BLE 버튼이 활성화 되었습니다."
                5 -> "IR 버튼이 비활성화 되었습니다."
                6 -> "IR 버튼이 활성화 되었습니다."
                else -> ""
            },
            fontSize = 16.sp,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
                .padding(top = 10.dp, start = 20.dp, end = 20.dp)
        )
    }
}

@Preview
@Composable
private fun ControllerBLEViewPreview() {
    NordicTheme {
        ControllerBLEView()
    }
}