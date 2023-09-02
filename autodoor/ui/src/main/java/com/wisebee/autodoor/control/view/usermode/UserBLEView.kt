package com.wisebee.autodoor.control.view.usermode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val macAddress = remember { mutableStateListOf<Byte>( 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 ) }
    var battery by remember { mutableStateOf( 3300 ) }
    val bPressed = remember { mutableStateListOf( false, false ) }
    if(packet.value[0] == DataToMCU.FID_APP_BLE_COMMAND && packet.value[1].toInt() >= 3) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed.fill(false)

        when(packet.value[2]) {
            DataToMCU.CMD_GET_BLE_STATUS -> {
                if(packet.value[1].toInt() >= (2 + 6 + 2 + 1)) {
                    for (i in 0 until 6)
                        macAddress[i] = packet.value[3 + i]
                    battery = ByteBuffer.wrap(packet.value, 9, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                    battery = battery.and(0x7fff)
                }
                nResult  = 0
            }
            DataToMCU.CMD_CLEAR_BUTTON_MAC -> nResult  = packet.value[3].toInt()
        }
    }
    val nPercent = if (battery < 0) 0
            else if(battery > 3000) 100
            else ((battery - 0f) * 100f / (3000f - 0f)).roundToInt()

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
                text = "MAC : " + String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                    macAddress[0], macAddress[1], macAddress[2], macAddress[3], macAddress[4], macAddress[5]),
                fontSize = 18.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
            StartButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .defaultMinSize(150.dp),
                pressed = bPressed[0],
                button = "초기화"
            ) {
                bPressed[0] = true
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_CLEAR_BUTTON_MAC)
            }

        }
        Text(
            text = when(nResult) {
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