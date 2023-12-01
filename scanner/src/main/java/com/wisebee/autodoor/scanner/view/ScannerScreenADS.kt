package com.wisebee.autodoor.scanner.view

import android.os.ParcelUuid
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import no.nordicsemi.android.common.ui.scanner.DeviceSelected
import no.nordicsemi.android.common.ui.scanner.ScannerScreenResult
import no.nordicsemi.android.common.ui.scanner.ScanningCancelled
import no.nordicsemi.android.common.ui.scanner.main.DeviceListItem
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import no.nordicsemi.android.common.ui.scanner.view.ScannerAppBar

@Composable
fun ScannerScreenADS(
    title: String,
    uuid: ParcelUuid?,
    cancellable: Boolean = true,
    onResult: (ScannerScreenResult, String) -> Unit,
    deviceItem: @Composable (DiscoveredBluetoothDevice) -> Unit = {
        DeviceListItem(it.displayName, it.address)
    }
) {
    var isScanning by rememberSaveable { mutableStateOf(false) }

    Column {
        if (cancellable) {
            ScannerAppBar(title, isScanning) { onResult(ScanningCancelled, "") }
        } else {
            ScannerAppBar(title, isScanning)
        }
        ScannerViewADS(
            uuid = uuid,
            onScanningStateChanged = { isScanning = it },
            onResult = { device, pw -> onResult(DeviceSelected(device), pw) },
            deviceItem = deviceItem,
        )
    }
}