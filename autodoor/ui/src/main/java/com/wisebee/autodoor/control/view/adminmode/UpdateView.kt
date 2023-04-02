package com.wisebee.autodoor.control.view.adminmode

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
import com.wisebee.autodoor.control.view.StartButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun UpdateView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nMode by remember { mutableStateOf(0) }
    var nCommand by remember { mutableStateOf(0) }
    var nResult by remember { mutableStateOf(0) }
    val bPressed = remember { mutableStateListOf( false, false, false ) }
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
                text = "업데이트 및 초기화", fontSize = 25.sp,
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
                .wrapContentHeight()
                .padding(bottom = 5.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(30.dp)
                    .padding(end = 30.dp)
            ) {
                Text(
                    text = "USB", fontSize = 22.sp,
                    modifier = Modifier.wrapContentWidth()
                )
                RadioButton(
                    selected = nMode == 0,
                    onClick = {
                        nMode = 0
                    }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(30.dp)
            ) {
                Text(
                    text = "Serial Flash", fontSize = 22.sp,
                    modifier = Modifier.wrapContentWidth()
                )
                RadioButton(
                    selected = nMode == 1,
                    onClick = {
                        nMode = 1
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (20.dp + 16.dp), start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "업데이트", fontSize = 22.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1f)
                    .padding(end = 10.dp)
            )
            StartButton(
                modifier = Modifier
                    .width(70.dp),
                pressed = bPressed[0],
                button = "시작"
            ) {
                bPressed[0] = true
                viewModel.sendCommand(
                    DataToMCU.FID_APP_SYS_COMMAND,
                    if(nMode == 0) DataToMCU.CMD_UPDATE_USB
                    else DataToMCU.CMD_UPDATE_SFLASH)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (20.dp + 16.dp), start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "사용자용 초기값", fontSize = 22.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1f)
                    .padding(end = 10.dp)
            )
            StartButton(
                modifier = Modifier
                    .width(70.dp),
                pressed = bPressed[1],
                button = "저장"
            ) {
                bPressed[1] = true
                viewModel.sendCommand(DataToMCU.FID_APP_SYS_COMMAND, DataToMCU.BLE_SAVE_SUBCONFIG)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (20.dp + 16.dp), start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "공장 초기화", fontSize = 22.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1f)
                    .padding(end = 10.dp)
            )
            StartButton(
                modifier = Modifier
                    .width(70.dp),
                pressed = bPressed[2],
                button = "시작"
            ) {
                bPressed[2] = true
                viewModel.sendCommand(DataToMCU.FID_APP_SYS_COMMAND, DataToMCU.CMD_SET_DEFAULT)
            }
        }

        Text(
            text = when(nResult) {
                DataToMCU.SYS_CMD_SUCCESS -> {
                    if(nCommand == DataToMCU.CMD_SET_DEFAULT.toInt())
                        "명령이 정상적으로 완료되었습니다."
                    else
                        "명령이 정상적으로 완료되었습니다. MCU가 리부팅됩니다."
                }
                DataToMCU.SYS_CMD_NO_BIN -> "지정된 위치에 보관된 펌웨어가 없습니다."
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
        UpdateView()
    }
}