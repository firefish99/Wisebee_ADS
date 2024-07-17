package com.wisebee.autodoor.control.view.usermode

import androidx.compose.foundation.layout.*
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
import com.wisebee.autodoor.control.view.RefreshButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

@Composable
internal fun OperStatView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nPowerCount = remember { mutableStateListOf(0, 0, 0, 3300, 3300) }
    var bNewVersion by remember { mutableStateOf(false) }
    val bEnabled = remember { mutableStateListOf( true, false ) }
    if(packet.value[0] == DataToMCU.FID_APP_OPER_STAT) {
        if(packet.value[1].toInt() >= (4*5 + 2)) {
            for (i in 0..4) {
                nPowerCount[i] =
                    ByteBuffer.wrap(packet.value, 2 + i * 4, 4).order(ByteOrder.BIG_ENDIAN).int
            }
            bNewVersion = true
            bEnabled[0] = nPowerCount[3].and(0xff0000) != 0
            bEnabled[1] = nPowerCount[4].and(0xff0000) != 0
            nPowerCount[3] = nPowerCount[3].and(0x7fff)
            nPowerCount[4] = nPowerCount[4].and(0x7fff)
        }
        else if(packet.value[1].toInt() >= (4*4 + 2)) {
            for (i in 0..3) {
                nPowerCount[i] =
                    ByteBuffer.wrap(packet.value, 2 + i * 4, 4).order(ByteOrder.BIG_ENDIAN).int
            }
            bNewVersion = false
            bEnabled[0] = true
            bEnabled[1] = false
            nPowerCount[3] = nPowerCount[3].and(0x7fff)
            nPowerCount[4] = 0
        }
    }
    //Timber.tag("OperStatView").e("${nPowerCount[0]}, ${nPowerCount[1]}, ${nPowerCount[2]}, ${nPowerCount[3]}")

    val nPercent1 = if (nPowerCount[3] < 0) 0
        else if(nPowerCount[3] > 3000) 100
        else ((nPowerCount[3] - 0f) * 100f / (3000f - 0f)).roundToInt()
    val nPercent2 = if (nPowerCount[4] < 0) 0
        else if(nPowerCount[4] > 3000) 100
        else ((nPowerCount[4] - 0f) * 100f / (3000f - 0f)).roundToInt()

    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "운행 조회", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(DataToMCU.FID_APP_OPER_STAT)
            }
        }

        Column (
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
        ) {
            Text(
                text = "전원 인가 횟수 : ${"%05d".format(nPowerCount[0])}", fontSize = 18.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
            )
            Text(
                text = "문 열림 횟수 : ${"%05d".format(nPowerCount[1])}", fontSize = 18.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(bottom = 20.dp)
            )
            Text(
                text = "문 충돌 횟수 : ${"%05d".format(nPowerCount[2])}", fontSize = 18.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(bottom = 20.dp)
            )
            Text(
                text = "실내버튼#1 배터리 : ${"%d".format(nPercent1)}%", fontSize = 18.sp,
                color = if(!bEnabled[0]) Color.Gray else if (nPowerCount[3] < 1000) Color.Red else Color.Unspecified,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(bottom = 0.dp)
            )
            if(bNewVersion) {
                Text(
                    text = "실내버튼#2 배터리 : ${"%d".format(nPercent2)}%", fontSize = 18.sp,
                    color = if (!bEnabled[1]) Color.Gray else if (nPowerCount[4] < 1000) Color.Red else Color.Unspecified,
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .wrapContentWidth()
                        .padding(bottom = 0.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun OperStatViewPreview() {
    NordicTheme {
        OperStatView()
    }
}