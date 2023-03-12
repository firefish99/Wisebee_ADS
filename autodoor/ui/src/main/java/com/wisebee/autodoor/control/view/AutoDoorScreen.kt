package com.wisebee.autodoor.control.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.R
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import com.wisebee.autodoor.spec.AutoDoor
import com.wisebee.autodoor.spec.AutoDoorSpec
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.common.ui.scanner.view.DeviceConnectingView
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AutoDoorScreen(
    onNavigateUp: () -> Unit,
    password: String,
) {
    val viewModel: AutoDoorViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var befState by remember { mutableStateOf(AutoDoor.State.LOADING) }
    var pwState by remember { mutableStateOf(AutoDoor.AuthorizedPw.PREPARE) }
    val packet = viewModel.rxPacket.collectAsStateWithLifecycle()
    var nMode by remember { mutableStateOf(0) }
    val displayView by viewModel.displayView.collectAsStateWithLifecycle()

    Timber.tag("AutoDoorScreen").e("name=%s, pw=%s, state=%s", viewModel.deviceName, password, state.name)

    fun onBackPressed() {
        when(displayView) {
            AutoDoor.DisplayView.VIEW_MAIN -> onNavigateUp()
            AutoDoor.DisplayView.VIEW_USER_MODE,
            AutoDoor.DisplayView.VIEW_ADMIN_AUTH,
            AutoDoor.DisplayView.VIEW_ADMIN_MODE -> viewModel.setDisplay(AutoDoor.DisplayView.VIEW_MAIN)
            AutoDoor.DisplayView.VIEW_OPER_STAT,
            AutoDoor.DisplayView.VIEW_VERSION,
            AutoDoor.DisplayView.VIEW_CHANGE_MODE,
            AutoDoor.DisplayView.VIEW_RENAME_DEVICE,
            AutoDoor.DisplayView.VIEW_USER_PARAM,
            AutoDoor.DisplayView.VIEW_CHANGE_PW -> viewModel.setDisplay(AutoDoor.DisplayView.VIEW_USER_MODE)
            AutoDoor.DisplayView.VIEW_ADMIN_PARAM,
            AutoDoor.DisplayView.VIEW_CALIBRATION,
            AutoDoor.DisplayView.VIEW_UPDATE,
            AutoDoor.DisplayView.VIEW_OPER_INIT,
            AutoDoor.DisplayView.VIEW_TEST_MODE -> viewModel.setDisplay(AutoDoor.DisplayView.VIEW_ADMIN_MODE)
            AutoDoor.DisplayView.VIEW_INIT_TIME,
            AutoDoor.DisplayView.VIEW_SENSOR_ENABLE,
            AutoDoor.DisplayView.VIEW_TOF,
            AutoDoor.DisplayView.VIEW_RADAR,
            AutoDoor.DisplayView.VIEW_MAIN_BLE,
            AutoDoor.DisplayView.VIEW_DCM,
            AutoDoor.DisplayView.VIEW_BLDC,
            AutoDoor.DisplayView.VIEW_HLED,
            AutoDoor.DisplayView.VIEW_TIME -> viewModel.setDisplay(AutoDoor.DisplayView.VIEW_ADMIN_PARAM)
        }
    }

    BackHandler(enabled = true) {
        onBackPressed()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NordicAppBar(
            text = viewModel.deviceName,
            onNavigationButtonClick = { onBackPressed() },
            actions = {
                Box(
                    modifier = Modifier
                        .padding(end=10.dp)
                        .background(color = Color(0xff00c020),
                    shape = RoundedCornerShape(20.dp))
                        .width(24.dp)
                        .height(24.dp)
                )
            }
        )
        RequireBluetooth {
            when (state) {
                AutoDoor.State.LOADING -> {
                    DeviceConnectingView(
                        modifier = Modifier.padding(16.dp),
                    ) { padding ->
                        Button(
                            onClick = onNavigateUp,
                            modifier = Modifier.padding(padding),
                        ) {
                            Text(text = stringResource(id = R.string.action_cancel))
                        }
                    }
                }
                AutoDoor.State.READY -> {
                    befState = state
                    when(pwState) {
                        AutoDoor.AuthorizedPw.PREPARE -> {
                            val aPw = ByteArray(6).let { dest ->
                                password.toByteArray().let { src ->
                                    src.copyInto(dest, 0, 0, Integer.min(6, src.size))
                                }}
                            val aVer = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(AutoDoorSpec.versionNum.toShort()).array()
                            viewModel.sendCommand(DataToMCU.FID_APP_AUTH_USER_PW, byteArrayOf(*aPw, *aVer))
                            pwState = AutoDoor.AuthorizedPw.REQUESTED
                            AuthorizingView(modifier = Modifier.padding(16.dp), onClick = onNavigateUp)
                        }
                        AutoDoor.AuthorizedPw.REQUESTED -> {
                            if(packet.value[0] == DataToMCU.FID_APP_AUTH_USER_PW && packet.value[1].toInt() >= (1 + 2)) {
                                nMode = packet.value[2].toInt()
                                pwState = if(nMode == 1) AutoDoor.AuthorizedPw.SUCCESS
                                            else AutoDoor.AuthorizedPw.FAIL
                            }
                            AuthorizingView(modifier = Modifier.padding(16.dp), onClick = onNavigateUp)
                        }
                        AutoDoor.AuthorizedPw.SUCCESS -> AutoDoorMainView(onBackPressed = { onBackPressed() })
                        AutoDoor.AuthorizedPw.FAIL -> AuthorizeFailView(modifier = Modifier.padding(16.dp), onClick = onNavigateUp)
                    }
                }
                AutoDoor.State.NOT_AVAILABLE -> {
                    if(befState == AutoDoor.State.READY) {
                        if(pwState == AutoDoor.AuthorizedPw.FAIL)
                            AuthorizeFailView(modifier = Modifier.padding(16.dp), onClick = onNavigateUp)
                        else
                            onNavigateUp()
                    }
                    else {
                        Timber.tag("AutoDoorScreen").e("befState=%s", befState.name)
                        DeviceConnectingView(
                            modifier = Modifier.padding(16.dp),
                        ) { padding ->
                            Button(
                                onClick = onNavigateUp,
                                modifier = Modifier.padding(padding),
                            ) {
                                Text(text = stringResource(id = R.string.action_cancel))
                            }
                        }
                    }
                }
            }
        }
    }
}