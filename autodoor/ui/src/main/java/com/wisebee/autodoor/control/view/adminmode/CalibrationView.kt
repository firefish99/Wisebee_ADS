package com.wisebee.autodoor.control.view.adminmode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.view.RefreshButton
import com.wisebee.autodoor.control.view.StartButton
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
internal fun CalibrationView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nResult by remember { mutableStateOf(0) }
    var nProgress by remember { mutableStateOf(0) }
    val bPressed = remember { mutableStateListOf( *(Array(5) { false }) ) }
    if(packet.value[0] == DataToMCU.FID_APP_SENSOR_CALIB) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed.fill(false)
        if(packet.value[1].toInt() >= (6 + 2)) {
            nProgress =
                ByteBuffer.wrap(packet.value, 2 + 2, 2).order(ByteOrder.BIG_ENDIAN).short.toInt()
            if (packet.value[2] == DataToMCU.CMD_GET_CALIB) {
                if (packet.value[6].toInt() >= 7)
                    nResult = packet.value[6].toInt() + 2
                else
                    nResult = 0
            } else if (nProgress >= 100) nResult = 1
            else nResult = packet.value[6].toInt() + 2
        }
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
                text = "센서 캘리브레이션", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    //.align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            RefreshButton {
                viewModel.sendCommand(DataToMCU.FID_APP_SENSOR_CALIB, DataToMCU.CMD_GET_CALIB)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "모터 거리 측정", fontSize = 22.sp,
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
                viewModel.sendCommand(DataToMCU.FID_APP_SENSOR_CALIB, byteArrayOf(DataToMCU.CMD_SET_CALIB, DataToMCU.ID_CALIB_MOTOR))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "TOF/Radar 동시 측정", fontSize = 22.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1f)
                    .padding(end = 10.dp)
            )
            StartButton(
                modifier = Modifier
                    .width(70.dp),
                pressed = bPressed[1],
                button = "시작"
            ) {
                bPressed[1] = true
                viewModel.sendCommand(DataToMCU.FID_APP_SENSOR_CALIB, byteArrayOf(DataToMCU.CMD_SET_CALIB, DataToMCU.ID_CALIB_ALL))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "상부안전센서(TOF1) 거리 측정", fontSize = 22.sp,
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
                viewModel.sendCommand(DataToMCU.FID_APP_SENSOR_CALIB, byteArrayOf(DataToMCU.CMD_SET_CALIB, DataToMCU.ID_CALIB_TOF1))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "상부안전센서(TOF2) 거리 측정", fontSize = 22.sp,
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
                viewModel.sendCommand(DataToMCU.FID_APP_SENSOR_CALIB, byteArrayOf(DataToMCU.CMD_SET_CALIB, DataToMCU.ID_CALIB_TOF2))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
        ) {
            Text(
                text = "상부센서(Radar) 환경 측정", fontSize = 22.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1f)
                    .padding(end = 10.dp)
            )
            StartButton(
                modifier = Modifier
                    .width(70.dp),
                pressed = bPressed[4],
                button = "시작"
            ) {
                bPressed[4] = true
                viewModel.sendCommand(DataToMCU.FID_APP_SENSOR_CALIB, byteArrayOf(DataToMCU.CMD_SET_CALIB, DataToMCU.ID_CALIB_RADAR))
            }
        }

        Text(
            text = when(nResult) {
                1 -> "캘리브레이션이 완료 되었습니다."
                DataToMCU.CALIB_ERR_NONE -> "캘리브레이션이 시작 되었습니다."
                DataToMCU.CALIB_ERR_INIT -> "캘리브레이션을 시작할 수 없습니다.(초기화 진행중)"
                DataToMCU.CALIB_ERR_OTHER -> "캘리브레이션을 시작할 수 없습니다.(다른 캘리브레이션 진행중)"
                DataToMCU.CALIB_ERR_WAIT -> "캘리브레이션을 시작할 수 없습니다.(캘리브레이션 대기중)"
                DataToMCU.CALIB_ERR_SENSOR -> "캘리브레이션을 시작할 수 없습니다.(센서 활성화 안됨)"
                DataToMCU.CALIB_ERR_DOING -> "캘리브레이션 도중 센서 에러로 중지되었습니다."
                DataToMCU.CALIB_ERR_ERROR -> "캘리브레이션을 시작할 수 없습니다.(센서 에러)"
                DataToMCU.CALIB_ERR_STOP -> "캘리브레이션 도중 다른 명령으로 중지되었습니다."
                DataToMCU.CALIB_OK_ING -> {
                    if(nProgress == 0)
                        "캘리브레이션이 시작 되었습니다."
                    else
                        "캘리브레이션이 진행중입니다."
                }
                DataToMCU.CALIB_OK_DONE -> "캘리브레이션이 완료 되었습니다."
                else -> ""
            },
            fontSize = 16.sp,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
                .padding(top = 10.dp, start = 20.dp, end = 20.dp)
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp),
                trackColor = Color.LightGray,
                color = Color(0xff00a0a0),
                progress = nProgress / 100f
            )
        }
    }
}

@Preview
@Composable
private fun CalibrationViewPreview() {
    NordicTheme {
        CalibrationView()
    }
}