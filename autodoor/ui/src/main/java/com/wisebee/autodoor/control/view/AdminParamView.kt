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
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun AdminParamView() {
    //val viewModel: AutoDoorViewModel = hiltViewModel()
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
                //viewModel.getStatus(DataToMCU.FID_APP_USER_PARAM, DataToMCU.CMD_GET_USER_PARAM)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_USER_PARAM)
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
        ) { Text(text = "이니셜 시간 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_CHANGE_PW)
            },
        ) { Text(text = "기능별 활성 비활성", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_RENAME_DEVICE, DataToMCU.CMD_GET_NAME)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_RENAME_DEVICE)
            },
        ) { Text(text = "상부안전센서1 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_OPER_STAT)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "상부안전센서2 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_OPER_STAT)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "상부센서 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_OPER_STAT)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "컨트롤보드 BLE 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_OPER_STAT)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "DC 모터 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_OPER_STAT)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "BLDC 모터 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_OPER_STAT)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "상부LED 설정", fontSize = 20.sp) }
        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            onClick = {
                //viewModel.getStatus(DataToMCU.FID_APP_OPER_STAT)
                //viewModel.setDisplay(AutoDoor.DisplayView.VIEW_OPER_STAT)
            },
        ) { Text(text = "컨트롤보드 시간 설정", fontSize = 20.sp) }
    }
}

@Preview
@Composable
private fun AdminParamViewPreview() {
    NordicTheme {
        AdminParamView()
    }
}