package com.wisebee.autodoor.control

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import androidx.hilt.navigation.compose.hiltViewModel
import com.wisebee.autodoor.control.view.AutoDoorScreen
import kotlinx.parcelize.Parcelize
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel

val AutoDoor = createDestination<AutoDoorDevice, Unit>("AutoDoor")

@Parcelize
data class AutoDoorDevice(
    val device: BluetoothDevice,
    val name: String?,
    val pw: String,
): Parcelable

val AutoDoorDestination = defineDestination(AutoDoor) {
    val viewModel: SimpleNavigationViewModel = hiltViewModel()

    val doorDevice = viewModel.parameterOf(AutoDoor)

    AutoDoorScreen(
        onNavigateUp = { viewModel.navigateUp() },
        password = doorDevice.pw
    )
}
