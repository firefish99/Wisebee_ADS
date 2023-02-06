package com.wisebee.autodoor.scanner

import androidx.hilt.navigation.compose.hiltViewModel
import com.wisebee.autodoor.control.Blinky
import com.wisebee.autodoor.control.BlinkyDevice
import com.wisebee.autodoor.scanner.view.BlinkyScanner
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel

val Scanner = createSimpleDestination("scanner")

val ScannerDestination = defineDestination(Scanner) {
    val viewModel: SimpleNavigationViewModel = hiltViewModel()

    BlinkyScanner(
        onDeviceSelected = { device, name ->
            viewModel.navigateTo(Blinky, BlinkyDevice(device, name))
        }
    )
}