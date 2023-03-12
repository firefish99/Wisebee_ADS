package com.wisebee.autodoor.scanner

import androidx.hilt.navigation.compose.hiltViewModel
import com.wisebee.autodoor.control.AutoDoor
import com.wisebee.autodoor.control.AutoDoorDevice
import com.wisebee.autodoor.scanner.view.AutoDoorScanner
import com.wisebee.autodoor.spec.AutoDoorSpec
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel

val Scanner = createSimpleDestination("scanner")

val ScannerDestination = defineDestination(Scanner) {
    val viewModel: SimpleNavigationViewModel = hiltViewModel()

    AutoDoorScanner(
        onDeviceSelected = { device, name, pw ->
            AutoDoorSpec.bleDevice = device
            AutoDoorSpec.bleName = name
            viewModel.navigateTo(AutoDoor, AutoDoorDevice(device, name, pw))
        }
    )
}