package com.wisebee.autodoor.control.view

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
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import com.wisebee.autodoor.spec.AutoDoor
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun AdminModeView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()
    //Timber.tag("AdminModeView").e("start")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "관리자 모드", fontSize = 25.sp,
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
                viewModel.setDisplay(AutoDoor.DisplayView.VIEW_ADMIN_PARAM)
            },
        ) { Text(text = "기능설정변경", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_CHANGE_MODE, DataToMCU.CMD_GET_MODE)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_CHANGE_MODE)
            },
        ) { Text(text = "센서 캘리브레이션", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_CHANGE_PW)
            },
        ) { Text(text = "업데이트 및 초기화", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_RENAME_DEVICE, DataToMCU.CMD_GET_NAME)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_RENAME_DEVICE)
            },
        ) { Text(text = "운행조회 및 초기화", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_OPER_STAT)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "테스트 모드", fontSize = 20.sp) }
    }
}

@Preview
@Composable
private fun AdminModeViewPreview() {
    NordicTheme {
        AdminModeView()
    }
}