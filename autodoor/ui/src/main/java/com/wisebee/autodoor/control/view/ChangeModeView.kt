package com.wisebee.autodoor.control.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.RadioButton
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
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun ChangeModeView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nMode by remember { mutableStateOf(0) }
    if(packet.value[0] == DataToMCU.FID_APP_CHANGE_MODE && packet.value[1].toInt() >= (2 + 2)) {
        nMode = packet.value[3].toInt()
    }
    //Timber.tag("ModeChangeView").e("$nMode")

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
                text = "자동/수동 모드 변환", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    //.align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .padding(bottom = 10.dp)
            )
        }

        Column (
            verticalArrangement = Arrangement.spacedBy((-5).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text(
                    text = "자동 모드", fontSize = 22.sp,
                    modifier = Modifier.wrapContentWidth()
                )
                RadioButton(
                    selected = nMode == 0,
                    onClick = {
                        nMode = 0
                        viewModel.sendCommand(DataToMCU.FID_APP_CHANGE_MODE,
                            byteArrayOf(DataToMCU.CMD_CHANGE_MODE, nMode.toByte()))
                    }
                )
            }
            Text(
                text = "(자동 모드) : 모든 기능 사용", fontSize = 16.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
        }

        Column (
            verticalArrangement = Arrangement.spacedBy((-5).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text(
                    text = "수동 모드", fontSize = 22.sp,
                    modifier = Modifier.wrapContentWidth()
                )
                RadioButton(
                    selected = nMode == 1,
                    onClick = {
                        nMode = 1
                        viewModel.sendCommand(DataToMCU.FID_APP_CHANGE_MODE,
                            byteArrayOf(DataToMCU.CMD_CHANGE_MODE, nMode.toByte()))
                    }
                )
            }
            Text(
                text = "(수동 모드) : App 만 사용", fontSize = 16.sp,
                modifier = Modifier
                    .wrapContentWidth()
            )
        }
    }
}

@Preview
@Composable
private fun ChangeModeViewPreview() {
    NordicTheme {
        ChangeModeView()
    }
}