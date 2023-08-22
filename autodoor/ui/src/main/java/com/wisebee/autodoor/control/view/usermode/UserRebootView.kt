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
import com.wisebee.autodoor.control.view.StartButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun UserRebootView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nCommand by remember { mutableStateOf(0) }
    var nResult by remember { mutableStateOf(0) }
    val bPressed = remember { mutableStateListOf( false, false, false, false ) }
    if(packet.value[0] == DataToMCU.FID_APP_SYS_COMMAND) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed.fill(false)
        if(packet.value[1].toInt() >= (2 + 2)) {
            nCommand = packet.value[2].toInt()
            nResult = packet.value[3].toInt() + 1
        }
    }
    //Timber.tag("ModeChangeView").e("$nMode")

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
                text = "컨트롤러 리부팅", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    //.align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (20.dp + 16.dp), start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "컨트롤러 리부팅", fontSize = 22.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1f)
                    .padding(end = 10.dp)
            )
            StartButton(
                modifier = Modifier
                    .width(70.dp),
                pressed = bPressed[3],
                button = "시작"
            ) {
                bPressed[3] = true
                viewModel.sendCommand(DataToMCU.FID_APP_SYS_COMMAND, DataToMCU.BLE_SYS_RESET)
            }
        }

        Text(
            text = when(nResult) {
                DataToMCU.SYS_CMD_SUCCESS -> "명령이 정상적으로 완료되었습니다. MCU가 리부팅됩니다."
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
private fun UpdateViewPreview() {
    NordicTheme {
        UserRebootView()
    }
}