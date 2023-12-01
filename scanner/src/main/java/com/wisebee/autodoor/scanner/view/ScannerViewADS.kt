package com.wisebee.autodoor.scanner.view

import android.os.ParcelUuid
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.permission.RequireLocation
import no.nordicsemi.android.common.ui.scanner.main.DeviceListItem
import no.nordicsemi.android.common.ui.scanner.main.DevicesListView
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import no.nordicsemi.android.common.ui.scanner.repository.ScanningState

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ScannerViewADS(
    uuid: ParcelUuid?,
    onScanningStateChanged: (Boolean) -> Unit = {},
    onResult: (DiscoveredBluetoothDevice, String) -> Unit,
    deviceItem: @Composable (DiscoveredBluetoothDevice) -> Unit = {
        DeviceListItem(it.displayName, it.address)
    }
) {
    /*
    RequireBluetooth(
        onChanged = { onScanningStateChanged(it) }
    ) {
        RequireLocation(
            onChanged = { onScanningStateChanged(it) }
        ) { isLocationRequiredAndDisabled ->
            val viewModel = hiltViewModel<ScannerViewModel>()
                .apply { setFilterUuid(uuid) }

            val state by viewModel.state.collectAsStateWithLifecycle(ScanningState.Loading)
            val config by viewModel.filterConfig.collectAsStateWithLifecycle()
            var refreshing by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()
            fun refresh() = scope.launch {
                refreshing = true
                viewModel.refresh()
                delay(400) // TODO remove this delay and refreshing variable after updating material dependency
                refreshing = false
            }

            Column(modifier = Modifier.fillMaxSize()) {
                FilterView(
                    config = config,
                    onChanged = { viewModel.setFilter(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.appBarColor))
                        .padding(horizontal = 16.dp),
                )

                val pullRefreshState  = rememberPullRefreshState(
                    refreshing = refreshing,
                    onRefresh = { refresh() },
                )

                Box(modifier = Modifier.pullRefresh(pullRefreshState).clipToBounds()) {
                    DevicesListView(
                        isLocationRequiredAndDisabled = isLocationRequiredAndDisabled,
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        onClick = { onResult(it) },
                        deviceItem = deviceItem,
                    )

                    PullRefreshIndicator(
                        refreshing = refreshing,
                        state = pullRefreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }*/
    RequireBluetooth(
        onChanged = { onScanningStateChanged(it) }
    ) {
        RequireLocation(
            onChanged = { onScanningStateChanged(it) }
        ) { isLocationRequiredAndDisabled ->
            val viewModel = hiltViewModel<ScannerViewModelADS>()
                .apply { setFilterUuid(uuid) }

            val state by viewModel.state.collectAsStateWithLifecycle(ScanningState.Loading)
            //val config by viewModel.filterConfig.collectAsStateWithLifecycle()
            var refreshing by remember { mutableStateOf(false) }
            var selected : DiscoveredBluetoothDevice? by remember { mutableStateOf(null) }

            val scope = rememberCoroutineScope()
            fun refresh() = scope.launch {
                refreshing = true
                viewModel.refresh()
                delay(400) // TODO remove this delay and refreshing variable after updating material dependency
                refreshing = false
            }

            val (focusRequester) = FocusRequester.createRefs()
            var sPassword by remember { mutableStateOf("") }

            Column(modifier = Modifier.fillMaxSize()) {
                /*
                FilterView(
                    config = config,
                    onChanged = { viewModel.setFilter(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.appBarColor))
                        .padding(horizontal = 16.dp),
                )*/

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = refreshing,
                    onRefresh = { refresh() },
                )

                Box(modifier = Modifier.pullRefresh(pullRefreshState).clipToBounds().weight(1f)) {
                    DevicesListView(
                        isLocationRequiredAndDisabled = isLocationRequiredAndDisabled,
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        onClick = { /*onResult(it)*/selected = it },
                        deviceItem = {
                            DeviceListItem(name = it.displayName,
                                address = it.address,
                                modifier = if(it == selected) Modifier.background(Color.LightGray) else Modifier
                            )
                        },
                    )

                    PullRefreshIndicator(
                        refreshing = refreshing,
                        state = pullRefreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
                Column(modifier = Modifier
                    .padding(bottom = 40.dp)
                ) {
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
                    Button(
                        enabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        onClick = {
                            if(selected != null) {
                                onResult(selected!!, sPassword)
                            }
                            else {
                                if(state is ScanningState.DevicesDiscovered) {
                                    val list = state as ScanningState.DevicesDiscovered
                                    if(list.size() == 1) {
                                        onResult(list.devices[0], sPassword)
                                    }
                                }
                            }

                            //selected?.let { onResult(it, sPassword) }
                        },
                    ) { Text(text = "연 결", fontSize = 18.sp) }
                }
            }
        }
    }
}
