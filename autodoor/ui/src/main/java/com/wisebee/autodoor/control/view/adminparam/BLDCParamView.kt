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
internal fun BLDCParamView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nParamCount = 40
    val nParamValue = remember { mutableStateListOf( *(Array(nParamCount) { 0 }) ) }
    var bPressed by remember { mutableStateOf( false ) }
    if(packet.value[0] == DataToMCU.FID_APP_RW_BLOCK) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed = false
        if(packet.value[1].toInt() >= (2*nParamCount + 3)) {
            if (packet.value[2].and(DataToMCU.BLE_RW_CMD_MASK) == DataToMCU.BLE_RW_BLDC_CMD) {
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
                text = "BLDC 설정", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(
                    DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_BLDC_CMD.or(
                        DataToMCU.BLE_RW_READ))
            }
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 0.dp, start = 10.dp, end = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SimpleParamInput(name="최고 속도", value=nParamValue[0], valueRange=100f..4000f, unit=100) {nParamValue[0]=it.roundToInt()}
            SimpleParamInput(name="최저 속도", value=nParamValue[1], valueRange=100f..4000f, unit=100) {nParamValue[1]=it.roundToInt()}
            SimpleParamInput(name="가속도", value=nParamValue[2], valueRange=100f..1000f, unit=100) {nParamValue[2]=it.roundToInt()}
            SimpleParamInput(name="가속 단위시간", value=nParamValue[3], valueRange=100f..1000f,unit=100) {nParamValue[3]=it.roundToInt()}
            SimpleParamInput(name="감속도", value=nParamValue[4], valueRange=100f..1000f, unit=100) {nParamValue[4]=it.roundToInt()}
            SimpleParamInput(name="감속 단위시간", value=nParamValue[5], valueRange=100f..1000f,unit=100) {nParamValue[5]=it.roundToInt()}
            SimpleParamInput(name="P게인 분자", value=nParamValue[6], valueRange=10f..200f, unit=10) {nParamValue[6]=it.roundToInt()}
            SimpleParamInput(name="P게인 분모", value=nParamValue[7], valueRange=500f..2000f, unit=100) {nParamValue[7]=it.roundToInt()}
            SimpleParamInput(name="I게인 분자", value=nParamValue[8], valueRange=10f..200f, unit=10) {nParamValue[8]=it.roundToInt()}
            SimpleParamInput(name="I게인 분모", value=nParamValue[9], valueRange=1000f..20000f, unit=100) {nParamValue[9]=it.roundToInt()}
            SimpleParamInput(name="속도 LPF 계수 분자", value=nParamValue[10], valueRange=10f..200f, unit=5) {nParamValue[10]=it.roundToInt()}
            SimpleParamInput(name="속도 LPF 계수 분모", value=nParamValue[11], valueRange=100f..1000f, unit=100) {nParamValue[11]=it.roundToInt()}
            SimpleParamInput(name="열림 속도", value=nParamValue[12], valueRange=1000f..2800f, unit=100) {nParamValue[12]=it.roundToInt()}
            SimpleParamInput(name="열림 가속도", value=nParamValue[13], valueRange=100f..500f, unit=100) {nParamValue[13]=it.roundToInt()}
            SimpleParamInput(name="열림 가속 단위시간", value=nParamValue[14], valueRange=100f..1000f,unit=100) {nParamValue[14]=it.roundToInt()}
            SimpleParamInput(name="열림 감속도", value=nParamValue[15], valueRange=100f..500f, unit=100) {nParamValue[15]=it.roundToInt()}
            SimpleParamInput(name="열림 감속 단위시간", value=nParamValue[16], valueRange=100f..1000f,unit=100) {nParamValue[16]=it.roundToInt()}
            SimpleParamInput(name="열림 갭", value=nParamValue[17], valueRange=100f..1000f,unit=100) {nParamValue[17]=it.roundToInt()}
            SimpleParamInput(name="문닫힘 대기시간", value=nParamValue[18], valueRange=1000f..10000f,unit=100) {nParamValue[18]=it.roundToInt()}
            SimpleParamInput(name="닫힘 속도", value=nParamValue[19], valueRange=500f..2800f, unit=100) {nParamValue[19]=it.roundToInt()}
            SimpleParamInput(name="닫힘 가속도", value=nParamValue[20], valueRange=100f..500f, unit=100) {nParamValue[20]=it.roundToInt()}
            SimpleParamInput(name="닫힘 가속 단위시간", value=nParamValue[21], valueRange=100f..1000f,unit=100) {nParamValue[21]=it.roundToInt()}
            SimpleParamInput(name="닫힘 감속도", value=nParamValue[22], valueRange=100f..500f, unit=100) {nParamValue[22]=it.roundToInt()}
            SimpleParamInput(name="닫힘 감속 단위시간", value=nParamValue[23], valueRange=100f..1000f,unit=100) {nParamValue[23]=it.roundToInt()}
            SimpleParamInput(name="닫힘 갭", value=nParamValue[24], valueRange=50f..200f,unit=10) {nParamValue[24]=it.roundToInt()}
            SimpleParamInput(name="닫힘 파워", value=nParamValue[25], valueRange=0f..30f,unit=1) {nParamValue[25]=it.roundToInt()}
            SimpleParamInput(name="최종 닫힘 갭", value=nParamValue[38], valueRange=0f..200f,unit=10) {nParamValue[38]=it.roundToInt()}
            SimpleParamInput(name="최종 닫힘 파워", value=nParamValue[39], valueRange=0f..30f,unit=1) {nParamValue[39]=it.roundToInt()}
            SimpleParamInput(name="저속 속도", value=nParamValue[26], valueRange=300f..1500f,unit=100) {nParamValue[26]=it.roundToInt()}
            SimpleParamInput(name="저속 시작 구간", value=nParamValue[27], valueRange=500f..2000f,unit=100) {nParamValue[27]=it.roundToInt()}
            SimpleParamInput(name="충격 레벨", value=nParamValue[28], valueRange=500f..3000f,unit=100) {nParamValue[28]=it.roundToInt()}
            SimpleParamInput(name="충격 검지시간", value=nParamValue[29], valueRange=100f..500f,unit=100) {nParamValue[29]=it.roundToInt()}
            SimpleParamInput(name="충격 대기시간", value=nParamValue[30], valueRange=500f..3000f,unit=100) {nParamValue[30]=it.roundToInt()}
            SimpleParamInput(name="제로 속도", value=nParamValue[31], valueRange=300f..1500f,unit=100) {nParamValue[31]=it.roundToInt()}
            SimpleParamInput(name="제로 충격 레벨", value=nParamValue[32], valueRange=500f..3000f,unit=100) {nParamValue[32]=it.roundToInt()}
            SimpleParamInput(name="제로 충격 검지시간", value=nParamValue[33], valueRange=100f..500f,unit=100) {nParamValue[33]=it.roundToInt()}
            SimpleParamInput(name="제로 충격 대기시간", value=nParamValue[34], valueRange=2000f..5000f,unit=100) {nParamValue[34]=it.roundToInt()}
            SimpleParamInput(name="감속 판정 계수", value=nParamValue[35], valueRange=1f..60f,unit=1) {nParamValue[35]=it.roundToInt()}
            SimpleParamInput(name="Run Away 알람", value=nParamValue[36], valueRange=10f..100f,unit=5) {nParamValue[36]=it.roundToInt()}
            SimpleParamInput(name="강제 문열림 마진폭", value=nParamValue[37], valueRange=50f..200f,unit=10) {nParamValue[37]=it.roundToInt()}
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
                    byteArrayOf(DataToMCU.BLE_RW_BLDC_CMD.or(DataToMCU.BLE_RW_WRITE), *nSendParam))
            },
        ) { Text(text = "변경", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun BLDCParamViewPreview() {
    NordicTheme {
        BLDCParamView()
    }
}