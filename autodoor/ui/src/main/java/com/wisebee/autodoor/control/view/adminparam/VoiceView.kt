package com.wisebee.autodoor.control.view.adminparam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.WbTheme
import com.wisebee.autodoor.control.view.RefreshButton
import com.wisebee.autodoor.control.view.SimpleParamInput
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.roundToInt

@Composable
internal fun VoiceView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nPeriod by remember { mutableStateOf(0) }
    var sStartTime by remember { mutableStateOf("") }
    var sResetTime by remember { mutableStateOf("") }
    var bPressed by remember { mutableStateOf( false ) }
    var bEnabled by remember { mutableStateOf( false ) }
    if(packet.value[0] == DataToMCU.FID_APP_RW_BLOCK) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed = false
        if(packet.value[1].toInt() >= (6 + 2 + 6 + 3)) {
            if (packet.value[2].and(DataToMCU.BLE_RW_CMD_MASK) == DataToMCU.BLE_RW_VOICE_CMD) {
                bEnabled = true
                sStartTime = "%d-%02d-%02d,%02d:%02d:%02d".format(packet.value[3] + 2000, packet.value[4], packet.value[5], packet.value[6], packet.value[7], packet.value[8] )
                nPeriod = ByteBuffer.wrap(packet.value, 3 + 6, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                sResetTime = "%d-%02d-%02d,%02d:%02d:%02d".format(packet.value[11] + 2000, packet.value[12], packet.value[13], packet.value[14], packet.value[15], packet.value[16] )
            }
        }
    }
    //Timber.tag("BLDCParamView").e("${nParamValue[0]}, ${nParamValue[1]}, ${nParamValue[2]}")

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
                text = "음성인식 설정", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_VOICE_CMD.or(
                        DataToMCU.BLE_RW_READ))
            }
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 0.dp, start = 10.dp, end = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "리셋시작 : $sStartTime",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(start=10.dp)
                    .wrapContentWidth()
            )
            SimpleParamInput(name="리셋 주기(시간)", enabled = bEnabled, value=nPeriod, valueRange=1f..24f, unit=1) {nPeriod=it.roundToInt()}
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = WbTheme.getButtonContainer(bPressed),
                contentColor = WbTheme.getButtonContent(bPressed)),
            enabled = bEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                bPressed = true
                val nSendParam = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(nPeriod.toShort()).array()
                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK,
                    byteArrayOf(DataToMCU.BLE_RW_VOICE_CMD.or(DataToMCU.BLE_RW_WRITE), *nSendParam))
            },
        ) { Text(text = "변경", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun VoiceViewPreview() {
    NordicTheme {
        VoiceView()
    }
}