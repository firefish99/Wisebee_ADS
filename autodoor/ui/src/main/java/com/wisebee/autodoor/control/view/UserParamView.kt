package com.wisebee.autodoor.control.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
internal fun UserParamView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    val nParamCount = 6
    val nParamValue = remember { mutableStateListOf( *(Array(nParamCount) { 0 }) ) }
    val bParamChanged = remember { mutableStateListOf( *(Array(nParamCount) { false }) ) }
    if(packet.value[0] == DataToMCU.FID_APP_USER_PARAM && packet.value[1].toInt() >= (2*nParamCount + 3)) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        if(packet.value[2] == DataToMCU.CMD_GET_USER_PARAM) {
            for (i in 0 until nParamCount) {
                nParamValue[i] =
                    ByteBuffer.wrap(packet.value, 3 + i * 2, 2)
                        .order(ByteOrder.BIG_ENDIAN).short.toInt()
            }
        }
    }
    //Timber.tag("UserParamView").e("${nParamValue[0]}, ${nParamValue[1]}, ${nParamValue[2]}")

    fun sendUserParam(index: Int, value: Int) {
        viewModel.sendCommand(DataToMCU.FID_APP_USER_PARAM,
            byteArrayOf((DataToMCU.CMD_SET_USER_PARAM + index).toByte(),
                *ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(value.toShort()).array()))
    }
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
                text = "기능설정 변경", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 10.dp)
            )
            Box(
                modifier = Modifier
                    .clickable {
                        viewModel.getStatus(DataToMCU.FID_APP_USER_PARAM, DataToMCU.CMD_GET_USER_PARAM)
                    },
            ) { RefreshButton() }
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),

        ) {
            ParamInput(
                modifier = Modifier.padding(top = 0.dp, bottom = 16.dp),
                name = "열림속도 : ",
                value = nParamValue[0],
                button = "전 송",
                onClick = {
                    bParamChanged[0] = false
                    sendUserParam(0, nParamValue[0]) },
                onValueChange = {
                    bParamChanged[0] = true
                    nParamValue[0] = it.toInt() },
                valueRange = 1000f..2800f,
                steps = 17,
            )
            ParamInput(
                modifier = Modifier.padding(top = 0.dp, bottom = 16.dp),
                name = "닫힘속도 : ",
                value = nParamValue[1],
                button = "전 송",
                onClick = {
                    bParamChanged[1] = false
                    sendUserParam(1, nParamValue[1]) },
                onValueChange = {
                    bParamChanged[1] = true
                    nParamValue[1] = it.toInt() },
                valueRange = 1000f..2800f,
                steps = 17,
            )
            ParamInput(
                modifier = Modifier.padding(top = 0.dp, bottom = 16.dp),
                name = "열림 갭 : ",
                value = nParamValue[2],
                button = "전 송",
                onClick = {
                    bParamChanged[2] = false
                    sendUserParam(2, nParamValue[2]) },
                onValueChange = {
                    bParamChanged[2] = true
                    nParamValue[2] = it.toInt() },
                valueRange = 100f..1000f,
                steps = 8,
            )
            ParamInput(
                modifier = Modifier.padding(top = 0.dp, bottom = 16.dp),
                name = "닫힘 갭 : ",
                value = nParamValue[3],
                button = "전 송",
                onClick = {
                    bParamChanged[3] = false
                    sendUserParam(3, nParamValue[3]) },
                onValueChange = {
                    bParamChanged[3] = true
                    nParamValue[3] = it.toInt() },
                valueRange = 50f..100f,
                steps = 4,
            )
            ParamInput(
                modifier = Modifier.padding(top = 0.dp, bottom = 16.dp),
                name = "닫힘 대기 시간 : ",
                value = nParamValue[4],
                button = "전 송",
                onClick = {
                    bParamChanged[4] = false
                    sendUserParam(4, nParamValue[4]) },
                onValueChange = {
                    bParamChanged[4] = true
                    nParamValue[4] = it.toInt() },
                valueRange = 1000f..10000f,
                steps = 89,
            )
            ParamInput(
                modifier = Modifier.padding(top = 0.dp, bottom = 16.dp),
                name = "저속 구간 시작 : ",
                value = nParamValue[5],
                button = "전 송",
                onClick = {
                    bParamChanged[5] = false
                    sendUserParam(5, nParamValue[5]) },
                onValueChange = {
                    bParamChanged[5] = true
                    nParamValue[5] = it.toInt() },
                valueRange = 500f..2000f,
                steps = 14,
            )
        }
    }
}

@Preview
@Composable
private fun UserParamViewPreview() {
    NordicTheme {
        UserParamView()
    }
}