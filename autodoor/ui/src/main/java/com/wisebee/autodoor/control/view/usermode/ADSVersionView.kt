package com.wisebee.autodoor.control.view.usermode

import androidx.compose.foundation.layout.*
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
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
internal fun ADSVersionView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var sModelName by remember { mutableStateOf("") }
    val nVersionNum = remember { mutableStateListOf(0, 0, 0, 0, 0, 0) }
    var bNewVersion by remember { mutableStateOf(false) }
    if(packet.value[0] == DataToMCU.FID_APP_VERSION) {
        if(packet.value[1].toInt() >= (20 + 4*6 + 2)) {         // BLE-main : 1.2.1 부터
            bNewVersion = true
            sModelName = String(packet.value, 2, 20).substringBefore('\u0000')
            for (i in 0..5)
                nVersionNum[i] =
                    ByteBuffer.wrap(packet.value, 2 + 20 + i * 4, 4).order(ByteOrder.BIG_ENDIAN).int
        }
        else if(packet.value[1].toInt() >= (20 + 4*3 + 2)) {    // BLE-main : 1.2.1 이전 버전
            bNewVersion = false
            sModelName = String(packet.value, 2, 20).substringBefore('\u0000')
            for (i in 0..2)
                nVersionNum[i] =
                    ByteBuffer.wrap(packet.value, 2 + 20 + i * 4, 4).order(ByteOrder.BIG_ENDIAN).int
            nVersionNum[3] = 0
            nVersionNum[4] = 0
            nVersionNum[5] = 0
        }
    }
    //Timber.tag("ADSVersionView").e("$sModelName, ${nVersionNum[0]}, ${nVersionNum[1]}, ${nVersionNum[2]}")

    fun getVerString(value: Int) : String
    {
        return "${(value / 10000)}.${((value / 100) % 100)}.${(value % 100)}"
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(bottom = (10.dp + 16.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Version 정보", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    //.align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .padding(bottom = 10.dp)
            )
        }

        Column (
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
        ) {
            Text(
                text = "모델명 : $sModelName", fontSize = 18.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
            )
            Text(
                text = "부트로더 버전 : ${getVerString(nVersionNum[0])}", fontSize = 18.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(bottom = 20.dp)
            )
            Text(
                text = "메인보드 버전 : ${getVerString(nVersionNum[1])}", fontSize = 18.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(bottom = 20.dp)
            )
            Text(
                text = "시리얼플래쉬 버전 : ${getVerString(nVersionNum[2])}", fontSize = 18.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(bottom = 0.dp)
            )
            if(bNewVersion) {
                Text(
                    text = "BLE 메인 버전 : ${getVerString(nVersionNum[3])}", fontSize = 18.sp,
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .wrapContentWidth()
                        .padding(top = 20.dp, bottom = 20.dp)
                )
                Text(
                    text = "BLE 버튼#1 버전 : ${getVerString(nVersionNum[4])}", fontSize = 18.sp,
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .wrapContentWidth()
                        .padding(bottom = 20.dp)
                )
                Text(
                    text = "BLE 버튼#2 버전 : ${getVerString(nVersionNum[5])}", fontSize = 18.sp,
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .wrapContentWidth()
                        .padding(bottom = 0.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun ADSVersionViewPreview() {
    NordicTheme {
        ADSVersionView()
    }
}