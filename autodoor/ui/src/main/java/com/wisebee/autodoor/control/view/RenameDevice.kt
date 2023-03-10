package com.wisebee.autodoor.control.view

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
    if(packet.value[0] == DataToMCU.FID_APP_RENAME_DEVICE && packet.value[1].toInt() >= (20 + 1 + 2)) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        nMode = packet.value[2].toInt()
        sDeviceName = String(packet.value, 3, 20).substringBefore('\u0000')
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
                text = "???????????? ??????", fontSize = 25.sp,
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
                text = "????????? ?????? ??????", fontSize = 16.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(top = 20.dp, start = 0.dp)
            )
            TextField(
                value = sDeviceName,
                onValueChange = { if (it.length <= 16 && it.matches(pattern)) sDeviceName = it },
                label = { Text("????????? ?????? ??????") },
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
                text = "?????? ??????", fontSize = 16.sp,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .wrapContentWidth()
                    .padding(start = 0.dp)
            )
            TextField(
                value = sPassword,
                onValueChange = { if (it.length <= 6) sPassword = it },
                label = { Text("Password 6??????") },
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
                1 -> "??????????????? ?????????????????????."
                2 -> "??????????????? ?????? ?????????????????????."
                3 -> "????????? ????????? ?????????????????????."
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
                containerColor = WbTheme.setButtonContainer,
                contentColor = WbTheme.setButtonContent),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 100.dp)
                .padding(bottom = 20.dp),
            onClick = {
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
        ) { Text(text = "??????", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun RenameDeviceViewPreview() {
    NordicTheme {
        RenameDeviceView()
    }
}