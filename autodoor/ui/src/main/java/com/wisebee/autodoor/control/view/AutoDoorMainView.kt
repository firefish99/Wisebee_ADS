package com.wisebee.autodoor.control.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisebee.autodoor.control.viewmodel.AutoDoorViewModel
import com.wisebee.autodoor.spec.AutoDoor
import com.wisebee.autodoor.spec.AutoDoorSpec
import timber.log.Timber

@Composable
internal fun AutoDoorMainView(
    onBackPressed: () -> Unit,
) {
    val viewModel: AutoDoorViewModel = hiltViewModel()
    val displayView by viewModel.displayView.collectAsStateWithLifecycle()

    Timber.tag("AutoDoorMainView").e("name=%s", viewModel.deviceName)
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                //.fillMaxSize()
                .widthIn(max = 460.dp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(top = 10.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "LUX HOME", fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .padding(bottom = 10.dp)
            )

            when (displayView) {
                AutoDoor.DisplayView.VIEW_MAIN -> AutoDoorStartView()
                AutoDoor.DisplayView.VIEW_USER_MODE -> UserModeView()
                AutoDoor.DisplayView.VIEW_OPER_STAT -> OperStatView()
                AutoDoor.DisplayView.VIEW_VERSION -> ADSVersionView()
                AutoDoor.DisplayView.VIEW_CHANGE_MODE -> ChangeModeView()
                AutoDoor.DisplayView.VIEW_RENAME_DEVICE -> RenameDeviceView()
                AutoDoor.DisplayView.VIEW_USER_PARAM -> UserParamView()
                AutoDoor.DisplayView.VIEW_CHANGE_PW -> ChangePWView()
                AutoDoor.DisplayView.VIEW_ADMIN_AUTH -> AdminAuthView()
                AutoDoor.DisplayView.VIEW_ADMIN_MODE -> AdminModeView()
                AutoDoor.DisplayView.VIEW_ADMIN_PARAM -> AdminParamView()
                else -> {}
            }
        }

        Column(modifier = Modifier
            .padding(bottom = 40.dp)
        ) {
            if(displayView != AutoDoor.DisplayView.VIEW_MAIN)
                Button(
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 100.dp),
                    onClick = onBackPressed,
                ) { Text(text = "돌아가기", fontSize = 14.sp) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "App Version " + AutoDoorSpec.versionName, fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .wrapContentWidth()
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}