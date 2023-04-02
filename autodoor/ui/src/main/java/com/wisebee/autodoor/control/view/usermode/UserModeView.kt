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