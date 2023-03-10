package com.wisebee.autodoor.control.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import com.wisebee.autodoor.spec.AutoDoor
import no.nordicsemi.android.common.theme.NordicTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun AdminAuthView() {
    val viewModel: AutoDoorViewModel = hiltViewModel()

    val (focusRequester) = FocusRequester.createRefs()

    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nMode by remember { mutableStateOf(1) }
    var sPassword by remember { mutableStateOf("") }
    if(packet.value[0] == DataToMCU.FID_APP_AUTH_ADMIN_PW && packet.value[1].toInt() >= (1 + 2)) {
        packet.value[0] = DataToMCU.FID_APP_NONE
        nMode = packet.value[2].toInt()
        if(nMode == 1) viewModel.setDisplay(AutoDoor.DisplayView.VIEW_ADMIN_MODE)
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Text(
            text = "관리자 모드", fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .wrapContentWidth()
                .padding(bottom = (10.dp + 16.dp))
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
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 8.dp, start = 20.dp, end = 20.dp)
                .focusRequester(focusRequester),
        )

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
                .padding(top = 15.dp, bottom = 5.dp)
        )
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = WbTheme.setButtonContainer,
                contentColor = WbTheme.setButtonContent),
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = {
                val aPass = ByteArray(6).let { dest ->
                    sPassword.toByteArray().let { src ->
                        src.copyInto(dest, 0, 0, Integer.min(6, src.size))
                    }}
                viewModel.sendCommand(DataToMCU.FID_APP_AUTH_ADMIN_PW, byteArrayOf(*aPass))
            },
        ) { Text(text = "모드 진입", fontSize = 16.sp) }
    }
}

@Preview
@Composable
private fun AdminAuthViewPreview() {
    NordicTheme {
        AdminAuthView()
    }
}