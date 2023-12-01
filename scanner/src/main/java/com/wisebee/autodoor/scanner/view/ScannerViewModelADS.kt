package com.wisebee.autodoor.scanner.view

import android.os.ParcelUuid
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import no.nordicsemi.android.common.ui.scanner.repository.ScannerRepository
import no.nordicsemi.android.common.ui.scanner.repository.ScanningState
import javax.inject.Inject

internal data class DevicesScanFilterADS(
    val filterUuidRequired: Boolean?,
    val filterNearbyOnly: Boolean,
    val filterWithNames: Boolean
)

private const val FILTER_RSSI = -50 // [dBm]

@HiltViewModel
internal class ScannerViewModelADS @Inject constructor(
    private val scannerRepository: ScannerRepository,
) : ViewModel() {
    private var uuid: ParcelUuid? = null

    val filterConfig = MutableStateFlow(
        DevicesScanFilterADS(
            filterUuidRequired = true,
            filterNearbyOnly = false,
            filterWithNames = true
        )
    )

    val state = filterConfig
        .combine(scannerRepository.getScannerState()) { config, result ->
            when (result) {
                is ScanningState.DevicesDiscovered -> result.applyFilters(config)
                else -> result
            }
        }
    // This can't be observed in View Model Scope, as it can exist even when the
    // scanner is not visible. Scanner state stops scanning when it is not observed.
    // .stateIn(viewModelScope, SharingStarted.Lazily, ScanningState.Loading)

    private fun ScanningState.DevicesDiscovered.applyFilters(config: DevicesScanFilterADS) =
        ScanningState.DevicesDiscovered(devices
            .filter {
                uuid == null ||
                        config.filterUuidRequired == false ||
                        it.scanResult?.scanRecord?.serviceUuids?.contains(uuid) == true
            }
            .filter { !config.filterNearbyOnly || it.highestRssi >= FILTER_RSSI }
            .filter { !config.filterWithNames || it.hadName }
        )

    fun setFilterUuid(uuid: ParcelUuid?) {
        this.uuid = uuid
        if (uuid == null) {
            filterConfig.value = filterConfig.value.copy(filterUuidRequired = null)
        }
    }

    fun setFilter(config: DevicesScanFilterADS) {
        this.filterConfig.value = config
    }

    fun refresh() {
        scannerRepository.clear()
    }

    override fun onCleared() {
        super.onCleared()
        scannerRepository.clear()
    }
}
