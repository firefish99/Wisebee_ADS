package com.wisebee.autodoor.control.view.adminmode

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
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
internal fun OperInitView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nPowerCount = remember { mutableStateListOf(0, 0, 0) }
    var bPressed by remember { mutableStateOf( false ) }
    if(packet.value[0] == DataToMCU.FID_APP_OPER_STAT && packet.value[1].toInt() >= (4*3 + 2)) {
        for( i in 0..2)
            nPowerCount[i] = ByteBuffer.wrap(packet.value, 2 + i * 4, 4).order(ByteOrder.BIG_ENDIAN).int
    }
    else if(packet.value[0] == DataToMCU.FID_APP_SYS_COMMAND) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed = false
        if(packet.value[1].toInt() >= (2 + 2)) {
            if (packet.value[2] == DataToMCU.CMD_INIT_OPER_STAT) {
                for (i in 0..2)
                    nPowerCount[i] = 0
            }
        }
    }
    //Timber.tag("OperStatView").e("${nPowerCount[0]}, ${nPowerCount[1]}, ${nPowerCount[2]}")

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
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
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = WbTheme.getButtonContainer(bPressed),
                contentColor = WbTheme.getButtonContent(bPressed)),
            enabled = true,
            modifier = Modifier
                .defaultMinSize(200.dp)
                .padding(horizontal = 10.dp),
            onClick = {
                bPressed = true
                viewModel.sendCommand(DataToMCU.FID_APP_SYS_COMMAND, DataToMCU.CMD_INIT_OPER_STAT)
            },
        ) { Text(text = "초기화", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun OperInitViewPreview() {
    NordicTheme {
        OperInitView()
    }
}