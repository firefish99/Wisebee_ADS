package com.wisebee.autodoor.control.view.usermode

import androidx.compose.foundation.layout.*
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
import com.wisebee.autodoor.control.view.StartButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

@Composable
internal fun UserBLEView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nResult by remember { mutableStateOf(0) }
    var nDispRes by remember { mutableStateOf(0) }
    val macAddress1 = remember { mutableStateListOf<Byte>( 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 ) }
    val macAddress2 = remember { mutableStateListOf<Byte>( 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 ) }
    var bNewVersion by remember { mutableStateOf(true) }
    val bEnabled = remember { mutableStateListOf( true, false ) }
    //var battery by remember { mutableStateOf( 3300 ) }
    val bPressed = remember { mutableStateListOf( false, false ) }
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
                }
                else if(packet.value[1].toInt() >= (2 + 6 + 2 + 1)) {           // BLE-main : 1.2.1 이전 버전
                    bNewVersion = false
                    for (i in 0 until 6)
                        macAddress1[i] = packet.value[3 + i]
                    //battery = ByteBuffer.wrap(packet.value, 9, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                    //battery = battery.and(0x7fff)
                }
                /*
                if(packet.value[1].toInt() >= (2 + 6 + 2 + 1)) {
                    for (i in 0 until 6)
                        macAddress[i] = packet.value[3 + i]
                    battery = ByteBuffer.wrap(packet.value, 9, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                    battery = battery.and(0x7fff)
                }*/
                nDispRes = nResult
                nResult  = 0
            }
            DataToMCU.CMD_CLEAR_BUTTON_MAC -> {
                nResult  = packet.value[3].toInt()
                nDispRes = nResult
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_GET_BLE_STATUS)
            }
        }
    }
    /*
    val nPercent = if (battery < 0) 0
            else if(battery > 3000) 100
            else ((battery - 0f) * 100f / (3000f - 0f)).roundToInt()
    */
    //Timber.tag("ControllerBLEView").e("${nParamValue[0]}, ${nParamValue[1]}, ${nParamValue[2]}")

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
                text = "Board BLE 설정", fontSize = 25.sp,
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
                .padding(top = 20.dp, bottom = 0.dp, start = 10.dp, end = 10.dp),
            //verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "MAC#1 : " + "%02X:%02X:%02X:%02X:%02X:%02X".format(macAddress1[0], macAddress1[1], macAddress1[2], macAddress1[3], macAddress1[4], macAddress1[5]),
                fontSize = 18.sp,
                color = if(!bEnabled[0]) Color.Gray else Color.Unspecified,
                modifier = Modifier
                    .width(260.dp)
            )
            if(bNewVersion)
                Text(
                    text = "MAC#2 : " + "%02X:%02X:%02X:%02X:%02X:%02X".format(macAddress2[0], macAddress2[1], macAddress2[2], macAddress2[3], macAddress2[4], macAddress2[5]),
                    fontSize = 18.sp,
                    color = if(!bEnabled[1]) Color.Gray else Color.Unspecified,
                    modifier = Modifier
                        .width(260.dp)
                )

            StartButton(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .defaultMinSize(150.dp),
                pressed = bPressed[0],
                button = "초기화"
            ) {
                bPressed[0] = true
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_CLEAR_BUTTON_MAC)
            }

        }
        Text(
            text = when(nDispRes) {
                1 -> "BLE Button MAC 주소가 초기화되었습니다."
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
private fun UserBLEViewPreview() {
    NordicTheme {
        UserBLEView()
    }
}