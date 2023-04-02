package com.wisebee.autodoor.control.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.WbTheme
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import com.wisebee.autodoor.spec.AutoDoor
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun AutoDoorStartView() {
    @Composable
    fun GetColor(state: Boolean) : ButtonColors {
        return  if(state)
                    ButtonDefaults.buttonColors(
                        containerColor = WbTheme.selectContainer,
                        contentColor = WbTheme.selectContent)
                else
                    ButtonDefaults.buttonColors()
    }

    val viewModel: AutoDoorViewModel = hiltViewModel()
    //val doorState by viewModel.doorState.collectAsStateWithLifecycle()
    var doorState by remember { mutableStateOf(0) }

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    if(packet.value[0] == DataToMCU.FID_APP_STATUS && packet.value[1].toInt() >= (1 + 2)) {
        doorState = packet.value[2].toInt()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Button(
            colors = GetColor((doorState and 0xff).toByte() == DataToMCU.STS_OPEN_DOOR),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = { viewModel.commandDoor(DataToMCU.CMD_OPEN_DOOR) },
        ) { Text(text = "문열기", fontSize = 20.sp) }
        Button(
            colors = GetColor((doorState and 0xff).toByte() == DataToMCU.STS_CLOSE_HOLD),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = { viewModel.commandDoor(DataToMCU.CMD_CLOSE_HOLD) },
        ) { Text(text = "닫힘 유지", fontSize = 20.sp) }
        Button(
            colors = GetColor((doorState and 0xff).toByte() == DataToMCU.STS_OPEN_HOLD),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = { viewModel.commandDoor(DataToMCU.CMD_OPEN_HOLD) },
        ) { Text(text = "열림 유지", fontSize = 20.sp) }
        Button(
            colors = GetColor((doorState and 0xff).toByte() == DataToMCU.STS_EM_STOP),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = { viewModel.commandDoor(DataToMCU.CMD_EM_STOP) },
        ) { Text(text = "긴급 정지", fontSize = 20.sp) }
        Button(
            colors = GetColor((doorState and 0xff).toByte() == DataToMCU.STS_HALF_OPEN),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = { viewModel.commandDoor(DataToMCU.CMD_HALF_OPEN) },
        ) { Text(text = "반개 유지", fontSize = 20.sp) }

        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = { viewModel.setDisplay(AutoDoor.DisplayView.VIEW_USER_MODE) },
        ) { Text(text = "사용자 모드", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = { viewModel.setDisplay(AutoDoor.DisplayView.VIEW_ADMIN_AUTH) },
        ) { Text(text = "관리자 모드", fontSize = 20.sp) }
    }
}

@Preview
@Composable
private fun AutoDoorStartViewPreview() {
    NordicTheme {
        AutoDoorStartView()
    }
}