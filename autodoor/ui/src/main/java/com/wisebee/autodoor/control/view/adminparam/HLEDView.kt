package com.wisebee.autodoor.control.view.adminparam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
internal fun HLEDView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nParamCount = 2
    val nParamValue = remember { mutableStateListOf( *(Array(nParamCount) { 0 }) ) }
    var bPressed by remember { mutableStateOf( false ) }
    if(packet.value[0] == DataToMCU.FID_APP_RW_BLOCK) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed = false
        if(packet.value[1].toInt() >= (2*nParamCount + 3)) {
            if (packet.value[2].and(DataToMCU.BLE_RW_CMD_MASK) == DataToMCU.BLE_RW_HLED_CMD) {
                for (i in 0 until nParamCount) {
                    nParamValue[i] =
                        ByteBuffer.wrap(packet.value, 3 + i * 2, 2)
                            .order(ByteOrder.BIG_ENDIAN).short.toInt()
                }
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
                text = "상부LED 설정", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_HLED_CMD.or(
                        DataToMCU.BLE_RW_READ))
            }
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 0.dp, start = 10.dp, end = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SimpleParamInput(name="상부LED 시작시간", value=nParamValue[0], valueRange=1000f..10000f, unit=100) {nParamValue[0]=it.roundToInt()}
            SimpleParamInput(name="상부LED 지속시간", value=nParamValue[1], valueRange=1000f..10000f, unit=100) {nParamValue[1]=it.roundToInt()}
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = WbTheme.getButtonContainer(bPressed),
                contentColor = WbTheme.getButtonContent(bPressed)),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                bPressed = true
                val nSendParam = nParamValue.foldIndexed(ByteArray(nParamValue.size * 2)) { index, bytes, param ->
                    bytes.apply {
                        ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(param.toShort()).array().copyInto(bytes, index * 2)
                    }
                }
                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK,
                    byteArrayOf(DataToMCU.BLE_RW_HLED_CMD.or(DataToMCU.BLE_RW_WRITE), *nSendParam))
            },
        ) { Text(text = "변경", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun HLEDViewPreview() {
    NordicTheme {
        HLEDView()
    }
}