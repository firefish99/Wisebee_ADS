package com.wisebee.autodoor.control.view.usermode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.WbTheme
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import no.nordicsemi.android.common.theme.NordicTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun RenameDeviceView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val (focusRequester) = FocusRequester.createRefs()
    val pattern = remember { Regex("[~!@#$%^&*()-_=+\\[\\]{};:'\",.<>?/\\w\\s]*") }

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nMode by remember { mutableStateOf(0) }
    var sDeviceName by remember { mutableStateOf("") }
    var sPassword by remember { mutableStateOf("") }
    var bPressed by remember { mutableStateOf( false ) }
    if(packet.value[0] == DataToMCU.FID_APP_RENAME_DEVICE) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        bPressed = false
        if(packet.value[1].toInt() >= (20 + 1 + 2)) {
            nMode = packet.value[2].toInt()
            sDeviceName = String(packet.value, 3, 20).substringBefore('\u0000')
        }
    }
    //Timber.tag("RenameDeviceView").e("name=$sDeviceName")

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
                text = "장치이름 변경", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    //.align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .padding(bottom = (10.dp + 16.dp))
            )
        }

        Column (
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
        ) {
            Text(
                text = "변경할 장치 이름", fontSize = 16.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(top = 20.dp, start = 0.dp)
            )
            TextField(
                value = sDeviceName,
                onValueChange = { if (it.length <= 16 && it.matches(pattern)) sDeviceName = it },
                label = { Text("변경할 이름 입력") },
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                keyboardActions = KeyboardActions(
                    onDone = { focusRequester.requestFocus() }
                ),
                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = (20.dp + 16.dp), start = 0.dp)
                    .onKeyEvent {
                        if (it.key == Key.Enter)
                            focusRequester.requestFocus()
                        false
                    }
            )

            Text(
                text = "비밀 번호", fontSize = 16.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(start = 0.dp)
            )
            TextField(
                value = sPassword,
                onValueChange = { if (it.length <= 6) sPassword = it },
                label = { Text("Password 6자리") },
                singleLine = true,
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation('*'),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = (20.dp + 16.dp), start = 0.dp)
                    .focusRequester(focusRequester),
            )
        }

        Text(
            text = when(nMode) {
                1 -> "장치이름이 변경되었습니다."
                2 -> "비밀번호가 잘못 입력되었습니다."
                3 -> "데이터 형식이 잘못되었습니다."
                else -> ""
            },
            fontSize = 16.sp,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
                .padding(bottom = 5.dp)
        )

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = WbTheme.getButtonContainer(bPressed),
                contentColor = WbTheme.getButtonContent(bPressed)),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 100.dp),
            onClick = {
                bPressed = true
                val aName = ByteArray(20).let { dest ->
                    sDeviceName.toByteArray().let { src ->
                        src.copyInto(dest, 0, 0, Integer.min(20, src.size))
                    }}
                val aPass = ByteArray(6).let { dest ->
                    sPassword.toByteArray().let { src ->
                        src.copyInto(dest, 0, 0, Integer.min(6, src.size))
                    }}
                viewModel.sendCommand(DataToMCU.FID_APP_RENAME_DEVICE, byteArrayOf(DataToMCU.CMD_CHANGE_NAME, *aName, *aPass))
            },
        ) { Text(text = "변경", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun RenameDeviceViewPreview() {
    NordicTheme {
        RenameDeviceView()
    }
}