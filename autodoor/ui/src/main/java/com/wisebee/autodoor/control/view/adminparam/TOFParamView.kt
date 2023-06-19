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
import com.wisebee.autodoor.control.view.SimpleParamDisplay
import com.wisebee.autodoor.control.view.SimpleParamInput
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.roundToInt

@Composable
internal fun TOFParamView(tof_id : Int = 0) {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nParamCount = 14
    val nParamValue = remember { mutableStateListOf( *(Array(nParamCount) { 0 }) ) }
    var bPressed by remember { mutableStateOf( false ) }
    if(packet.value[0] == DataToMCU.FID_APP_RW_BLOCK) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed = false
        if(packet.value[1].toInt() >= (2*nParamCount + 3)) {
            if (packet.value[2].and(DataToMCU.BLE_RW_CMD_MASK) == (DataToMCU.BLE_RW_TOF1_CMD + tof_id).toByte()) {
                for (i in 0 until nParamCount) {
                    nParamValue[i] =
                        ByteBuffer.wrap(packet.value, 3 + i * 2, 2)
                            .order(ByteOrder.BIG_ENDIAN).short.toInt()
                }
            }
        }
    }
    Timber.tag("TOFParamView").e("${nParamValue[0]}, ${nParamValue[1]}, ${nParamValue[2]}")

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
                text = "상부안전센서(TOF${tof_id + 1}) 설정", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK, (DataToMCU.BLE_RW_TOF1_CMD + tof_id).toByte().or(
                        DataToMCU.BLE_RW_READ))
            }
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 0.dp, start = 10.dp, end = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SimpleParamInput(name="물체 검지 시간", value=nParamValue[0], valueRange=50f..200f, unit=1) {nParamValue[0]=it.roundToInt()}
            SimpleParamInput(name="물체 검지 간격", value=nParamValue[1], valueRange=100f..400f, unit=10) {nParamValue[1]=it.roundToInt()}
            SimpleParamInput(name="검지영역 LX", value=nParamValue[2], valueRange=0f..15f, unit=1) {nParamValue[2]=it.roundToInt()}
            SimpleParamInput(name="검지영역 LY", value=nParamValue[3], valueRange=0f..15f, unit=1) {nParamValue[3]=it.roundToInt()}
            SimpleParamInput(name="검지영역 RX", value=nParamValue[4], valueRange=0f..15f, unit=1) {nParamValue[4]=it.roundToInt()}
            SimpleParamInput(name="검지영역 RY", value=nParamValue[5], valueRange=0f..15f, unit=1) {nParamValue[5]=it.roundToInt()}
            SimpleParamInput(name="인식 최대 거리", value=nParamValue[6], valueRange=1000f..4000f, unit=100) {nParamValue[6]=it.roundToInt()}
            SimpleParamInput(name="인식 최소 거리", value=nParamValue[7], valueRange=50f..300f, unit=10) {nParamValue[7]=it.roundToInt()}
            SimpleParamInput(name="인식 유효 시간", value=nParamValue[8], valueRange=100f..1000f, unit=100) {nParamValue[8]=it.roundToInt()}
            SimpleParamDisplay(name="환경 측정 거리 값", value=nParamValue[9].toString())
            Spacer(modifier=Modifier.padding(horizontal=5.dp))
            SimpleParamInput(name="환경 측정 유효화 범위", value=nParamValue[10], valueRange=50f..300f, unit=10) {nParamValue[10]=it.roundToInt()}
            SimpleParamInput(name="캘리브레이션 수행 시간", value=nParamValue[11], valueRange=5f..30f, unit=1) {nParamValue[11]=it.roundToInt()}
            ParamSwitch(name="캘리브레이션 데이터 유효성(수정 불가)", value=nParamValue[12] != 0)
            if(tof_id == 0) {
                Spacer(modifier=Modifier.padding(horizontal=5.dp))
                SimpleParamInput(name="자동문 인식 무시 영역", value=nParamValue[13], valueRange=100f..300f, unit=10) {nParamValue[13]=it.roundToInt()}
           }
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
                    byteArrayOf((DataToMCU.BLE_RW_TOF1_CMD + tof_id).toByte().or(DataToMCU.BLE_RW_WRITE), *nSendParam))
            },
        ) { Text(text = "변경", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun TOFParamViewPreview() {
    NordicTheme {
        TOFParamView(0)
    }
}