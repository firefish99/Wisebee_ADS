package com.wisebee.autodoor.control.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.control.repository.AutoDoorRepository
import com.wisebee.autodoor.spec.AutoDoor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.logger.NordicLogger
import javax.inject.Inject
import javax.inject.Named

/**
 * The view model for the AutoDoor screen.
 *
 * @param context The application context.
 * @property repository The repository that will be used to interact with the device.
 * @property deviceName The name of the Blinky device, as advertised.
 */
@HiltViewModel
class AutoDoorViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: AutoDoorRepository,
    val bleDevice : BluetoothDevice,
    @Named("deviceName") val deviceName: String,
    //navigator: Navigator,
) : AndroidViewModel(context as Application)/*, Navigator by navigator*/ {
    val state = repository.state
    val displayView = repository.loggedDisplayView
        .stateIn(viewModelScope, SharingStarted.Lazily, AutoDoor.DisplayView.VIEW_MAIN)
    val rxPacket = repository.loggedRxPacket
        .stateIn(viewModelScope, SharingStarted.Lazily, byteArrayOf(DataToMCU.FID_APP_STATUS, 0x02, 0x11))

    init {
        connect()
    }

    fun connect() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            repository.connect()
        }
    }

    fun setDisplay(view: AutoDoor.DisplayView) {
        repository.setDisplay(view)
    }

    fun commandDoor(cmd: Byte) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            repository.commandDoor(cmd)
        }
    }

    fun sendCommand(fid: Byte, data: ByteArray) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            repository.sendCommand(fid, data)
        }
    }

    fun sendCommand(fid: Byte) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            repository.sendCommand(fid)
        }
    }

    fun sendCommand(fid: Byte, flag: Byte) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            repository.sendCommand(fid, flag)
        }
    }

    fun openLogger() {
        NordicLogger.launch(getApplication(), repository.sessionUri)
    }

    override fun onCleared() {
        super.onCleared()
        repository.release()
    }
}