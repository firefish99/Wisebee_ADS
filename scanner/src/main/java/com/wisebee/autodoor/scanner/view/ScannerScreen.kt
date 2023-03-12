package com.wisebee.autodoor.scanner.view

import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wisebee.autodoor.scanner.R
import com.wisebee.autodoor.spec.AutoDoorSpec
import no.nordicsemi.android.common.ui.scanner.DeviceSelected
import no.nordicsemi.android.common.ui.scanner.ScannerScreen

@Composable
fun AutoDoorScanner(
    onDeviceSelected: (BluetoothDevice, String?, String) -> Unit,
) {
    ScannerScreen(
        title = stringResource(id = R.string.scanner_title),
        uuid = ParcelUuid(AutoDoorSpec.AUTODOOR_SERVICE_UUID),
        cancellable = false,
        onResult = { result, pw ->
            when (result) {
                is DeviceSelected -> with(result.device) {
                    onDeviceSelected(device, name, pw)
                }
                else -> {}
            }
        }
    )
}
