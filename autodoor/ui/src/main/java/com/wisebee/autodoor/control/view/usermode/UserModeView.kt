package com.wisebee.autodoor.control.view.usermode

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
internal fun UserModeView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()
    //Timber.tag("UserModeView").e("start")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "사용자 모드", fontSize = 25.sp,
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
                viewModel.sendCommand(DataToMCU.FID_APP_USER_PARAM, DataToMCU.CMD_GET_USER_PARAM)
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_USER_PARAM)
            },
        ) { Text(text = "기능설정변경", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_CHANGE_MODE, DataToMCU.CMD_GET_MODE)
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_CHANGE_MODE)
            },
        ) { Text(text = "자동모드 / 수동모드", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_CHANGE_PW)
            },
        ) { Text(text = "블루투스 비밀번호", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_BLE_COMMAND, DataToMCU.CMD_GET_BLE_STATUS)
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_USER_MAIN_BLE)
            },
        ) { Text(text = "컨트롤보드 BLE 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RENAME_DEVICE, DataToMCU.CMD_GET_NAME)
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_RENAME_DEVICE)
            },
        ) { Text(text = "장치이름 변경", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_SENSOR_CALIB, DataToMCU.CMD_GET_CALIB)
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_USER_CALIBRATION)
            },
        ) { Text(text = "센서 캘리브레이션", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_USER_REBOOT)
            },
        ) { Text(text = "컨트롤러 리부팅", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_RW_BLOCK, DataToMCU.BLE_RW_HLED_CMD.or(DataToMCU.BLE_RW_READ))
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_USER_HLED)
            },
        ) { Text(text = "상부LED 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_OPER_STAT)
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "운행 조회", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                viewModel.sendCommand(DataToMCU.FID_APP_VERSION)
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_VERSION)
            },
        ) { Text(text = "Version 정보", fontSize = 20.sp) }
    }
}

@Preview
@Composable
private fun UserModeViewPreview() {
    NordicTheme {
        UserModeView()
    }
}