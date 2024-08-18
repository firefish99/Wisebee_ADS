package com.wisebee.autodoor.control.view.adminparam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import com.wisebee.autodoor.spec.AutoDoor
import no.nordicsemi.android.common.theme.NordicTheme
import kotlin.experimental.or

@Composable
internal fun AdminParamView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()
    //Timber.tag("AdminParamView").e("start")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "기능설정변경", fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
                .padding(bottom = 10.dp))
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_INITIAL_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_INITIAL_TIME)
            },
        ) { Text(text = "이니셜 시간 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_SENSOR_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_SENSOR_ENABLE)
            },
        ) { Text(text = "기능별 활성 비활성", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_TOF1_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_TOF1)
            },
        ) { Text(text = "상부안전센서1 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_TOF2_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_TOF2)
            },
        ) { Text(text = "상부안전센서2 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_VOICE_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_VOICE)
            },
        ) { Text(text = "음성인식 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_RADAR_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_RADAR)
            },
        ) { Text(text = "상부센서 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_GET_BLE_STATUS)
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_MAIN_BLE)
            },
        ) { Text(text = "보드,버튼 BLE 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_DCM_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_DCM)
            },
        ) { Text(text = "DC 모터 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_BLDC_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_BLDC)
            },
        ) { Text(text = "BLDC 모터 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_HLED_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_HLED)
            },
        ) { Text(text = "상부LED 설정", fontSize = 20.sp) }
    }
}

@Preview
@Composable
private fun AdminParamViewPreview() {
    NordicTheme {
        AdminParamView()
    }
}