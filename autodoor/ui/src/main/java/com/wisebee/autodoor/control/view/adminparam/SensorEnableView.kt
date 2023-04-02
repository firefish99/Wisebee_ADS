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
import com.wisebee.autodoor.control.view.ParamSwitch
import com.wisebee.autodoor.control.view.RefreshButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and
import kotlin.experimental.or

@Composable
internal fun SensorEnableView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nParamCount = 11
    val nParamValue = remember { mutableStateListOf( *(Array(nParamCount) { false }) ) }
    var bPressed by remember { mutableStateOf( false ) }
    if(packet.value[0] == DataToMCU.FID_APP_RW_BLOCK) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed = false
        if(packet.value[1].toInt() >= (2 + 3)) {
            if (packet.value[2].and(DataToMCU.BLE_RW_CMD_MASK) == DataToMCU.BLE_RW_SENSOR_CMD) {
                var mask = 0x0001
                val value =
                    ByteBuffer.wrap(packet.value, 3, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
                for (i in 0 until nParamCount) {
                    nParamValue[i] = value.and(mask) == mask
                    mask = mask.shl(1)
                }
            }
        }
    }
    //Timber.tag("SensorEnableView").e("${nParamValue[0]}, ${nParamValue[1]}, ${nParamValue[2]}")

    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(bottom = (10.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "기능별 활성화/비활성화", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_SENSOR_CMD.or(
                        DataToMCU.BLE_RW_READ))
            }
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 0.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            ParamSwitch(name="워치독 활성", value=nParamValue[0]) {nParamValue[0]=it}
            ParamSwitch(name="상부안전센서(TOF1) 활성", value=nParamValue[1]) {nParamValue[1]=it}
            ParamSwitch(name="상부안전센서(TOF2) 활성", value=nParamValue[2]) {nParamValue[2]=it}
            ParamSwitch(name="상부센서(Radar) 활성", value=nParamValue[3]) {nParamValue[3]=it}
            ParamSwitch(name="블루투스 활성", value=nParamValue[4]) {nParamValue[4]=it}
            ParamSwitch(name="DC 모터 활성", value=nParamValue[5]) {nParamValue[5]=it}
            ParamSwitch(name="BLDC 모터 활성", value=nParamValue[6]) {nParamValue[6]=it}
            ParamSwitch(name="음성인식 활성", value=nParamValue[7]) {nParamValue[7]=it}
            ParamSwitch(name="TOF1 캘리브레이션 활성", value=nParamValue[8]) {nParamValue[8]=it}
            ParamSwitch(name="TOF2 캘리브레이션 활성", value=nParamValue[9]) {nParamValue[9]=it}
            ParamSwitch(name="Radar 캘리브레이션 활성", value=nParamValue[10]) {nParamValue[10]=it}
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
                var mask = 0x0001
                var value = 0x0000
                for (i in 0 until nParamCount) {
                    if(nParamValue[i])
                        value = value.or(mask)
                    mask = mask.shl(1)
                }

                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK,
                    byteArrayOf(DataToMCU.BLE_RW_SENSOR_CMD.or(DataToMCU.BLE_RW_WRITE),
                        *ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(value.toShort()).array()))
            },
        ) { Text(text = "변경", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun SensorEnableViewPreview() {
    NordicTheme {
        SensorEnableView()
    }
}