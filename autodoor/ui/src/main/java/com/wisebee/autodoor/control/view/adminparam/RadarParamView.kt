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
import com.wisebee.autodoor.control.view.ChirpParamInput
import com.wisebee.autodoor.control.view.ParamSwitch
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
internal fun RadarParamView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nParamCount = 14
    val nParamValue = remember { mutableStateListOf( *(Array(nParamCount) { 0 }) ) }
    var bPressed by remember { mutableStateOf( false ) }
    if(packet.value[0] == DataToMCU.FID_APP_RW_BLOCK) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed = false
        if(packet.value[1].toInt() >= (2*nParamCount + 3)) {
            if (packet.value[2].and(DataToMCU.BLE_RW_CMD_MASK) == DataToMCU.BLE_RW_RADAR_CMD) {
                for (i in 0 until nParamCount) {
                    nParamValue[i] =
                        ByteBuffer.wrap(packet.value, 3 + i * 2, 2)
                            .order(ByteOrder.BIG_ENDIAN).short.toInt()
                }
            }
        }
    }
    //Timber.tag("RadarParamView").e("${nParamValue[0]}, ${nParamValue[1]}, ${nParamValue[2]}")

    Column (
        //modifier = Modifier
        //    .verticalScroll(rememberScrollState()),
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
                text = "상부센서(Radar) 설정", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_RADAR_CMD.or(
                        DataToMCU.BLE_RW_READ))
            }
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 0.dp, start = 10.dp, end = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SimpleParamInput(name="증폭 이득", value=nParamValue[0], valueRange=40f..60f, unit=10) {nParamValue[0]=it.roundToInt()}
            ChirpParamInput(name="스캔 주파수 지속 시간", value=nParamValue[1]) {nParamValue[1]=if(it.roundToInt() == 2310) 1100 else it.roundToInt()}
            SimpleParamInput(name="스캔 간격", value=nParamValue[2], valueRange=10f..1000f, unit=1) {nParamValue[2]=it.roundToInt()}
            SimpleParamInput(name="알파 계수", value=nParamValue[3], valueRange=0f..255f, unit=1) {nParamValue[3]=it.roundToInt()}
            SimpleParamInput(name="감지 민감도", value=nParamValue[4], valueRange=0f..4095f, unit=1) {nParamValue[4]=it.roundToInt()}
            SimpleParamInput(name="안정 계수", value=nParamValue[5], valueRange=0f..255f, unit=1) {nParamValue[5]=it.roundToInt()}
            SimpleParamInput(name="감지 최소 거리", value=nParamValue[6], valueRange=10f..2000f, unit=10) {nParamValue[6]=it.roundToInt()}
            SimpleParamInput(name="감지 최대 거리", value=nParamValue[7], valueRange=10f..2000f, unit=10) {nParamValue[7]=it.roundToInt()}
            SimpleParamInput(name="캘리브레이션 대기시간 1", value=nParamValue[8], valueRange=5f..20f, unit=1) {nParamValue[8]=it.roundToInt()}
            SimpleParamInput(name="캘리브레이션 대기시간 2", value=nParamValue[9], valueRange=5f..20f, unit=1) {nParamValue[9]=it.roundToInt()}
            SimpleParamInput(name="캘리브레이션 스캔시간", value=nParamValue[10], valueRange=1f..5f, unit=1) {nParamValue[10]=it.roundToInt()}
            SimpleParamInput(name="캘리브레이션 횟수", value=nParamValue[11], valueRange=5f..20f, unit=1) {nParamValue[11]=it.roundToInt()}
            ParamSwitch(name="캘리브레이션 데이터 적용 유무", value=nParamValue[12] != 0) {nParamValue[12]=if(it) 1 else 0}
            Spacer(modifier=Modifier.padding(horizontal=5.dp))
            ParamSwitch(name="캘리브레이션 데이터 유효성(수정 불가)", value=nParamValue[13] != 0)
            Spacer(modifier=Modifier.padding(horizontal=10.dp))
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
                    byteArrayOf(DataToMCU.BLE_RW_RADAR_CMD.or(DataToMCU.BLE_RW_WRITE), *nSendParam))
            },
        ) { Text(text = "변경", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun RadarParamViewPreview() {
    NordicTheme {
        RadarParamView()
    }
}