package com.wisebee.autodoor.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import com.wisebee.autodoor.ble.data.DataToMCU
import com.wisebee.autodoor.ble.data.FromMCUState
import com.wisebee.autodoor.spec.AutoDoor
import com.wisebee.autodoor.spec.AutoDoorSpec
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.asValidResponseFlow
import no.nordicsemi.android.ble.ktx.getCharacteristic
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import no.nordicsemi.android.ble.ktx.suspend
import timber.log.Timber

class AutoDoorManager(
    context: Context,
    device: BluetoothDevice
): AutoDoor by AutoDoorManagerImpl(context, device)

private class AutoDoorManagerImpl(
    context: Context,
    private val device: BluetoothDevice,
): BleManager(context), AutoDoor {
    private val scope = CoroutineScope(Dispatchers.IO)

    private var toMCUCharacteristic: BluetoothGattCharacteristic? = null
    private var fromMCUCharacteristic: BluetoothGattCharacteristic? = null

    private val _displayView = MutableStateFlow(AutoDoor.DisplayView.VIEW_MAIN)
    override val displayView = _displayView.asStateFlow()

    private val _rxPacket = MutableStateFlow(byteArrayOf(DataToMCU.FID_APP_STATUS, 0x02, 0x22))
    override val rxPacket = _rxPacket.asStateFlow()

    override val state = stateAsFlow()
        .map {
            when (it) {
                is ConnectionState.Connecting,
                is ConnectionState.Initializing -> AutoDoor.State.LOADING
                is ConnectionState.Ready -> AutoDoor.State.READY
                is ConnectionState.Disconnecting,
                is ConnectionState.Disconnected -> AutoDoor.State.NOT_AVAILABLE
            }
        }
        .stateIn(scope, SharingStarted.Lazily, AutoDoor.State.NOT_AVAILABLE)

    override suspend fun connect() {
        connect(device)
            .retry(5, 500)
            .useAutoConnect(true)
            .timeout(5000)
            .suspend()
    }

    override fun release() {
        scope.cancel()

        val wasConnected = isReady

        cancelQueue()

        if (wasConnected) {
            disconnect().enqueue()
        }
    }

    override fun setDisplay(view: AutoDoor.DisplayView) {
        _displayView.value = view
    }

    override suspend fun commandDoor(cmd: Byte) {
        writeCharacteristic(
            toMCUCharacteristic,
            DataToMCU.doorCmd(cmd),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).suspend()
    }

    override suspend fun sendCommand(fid: Byte, data: ByteArray) {
        writeCharacteristic(
            toMCUCharacteristic,
            DataToMCU.sendCommand(fid, data),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).suspend()
    }

    override suspend fun getStatus(fid: Byte) {
        writeCharacteristic(
            toMCUCharacteristic,
            DataToMCU.getStatus(fid),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).suspend()
    }

    override suspend fun getStatus(fid: Byte, flag: Byte) {
        writeCharacteristic(
            toMCUCharacteristic,
            DataToMCU.getStatus(fid, flag),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).suspend()
    }

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    override fun getMinLogPriority(): Int {
        // By default, the library logs only INFO or
        // higher priority messages. You may change it here.
        return Log.VERBOSE
    }

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        gatt.getService(AutoDoorSpec.AUTODOOR_SERVICE_UUID)?.apply {
            toMCUCharacteristic = getCharacteristic(
                AutoDoorSpec.AUTODOOR_LED_CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE
            )

            fromMCUCharacteristic = getCharacteristic(
                AutoDoorSpec.AUTODOOR_BUTTON_CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY
            )

            return toMCUCharacteristic != null && fromMCUCharacteristic != null
        }
        return false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initialize() {

        beginAtomicRequestQueue().add(requestMtu(250)).enqueue()

        val flow: Flow<FromMCUState> = setNotificationCallback(fromMCUCharacteristic)
            .asValidResponseFlow()

        scope.launch {
            flow.map { it.rxPacket }.collect { _rxPacket.tryEmit(it) }
        }

        enableNotifications(fromMCUCharacteristic)
            .enqueue()

        writeCharacteristic(
            toMCUCharacteristic,
            DataToMCU.getStatus(DataToMCU.FID_APP_STATUS),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    override fun onServicesInvalidated() {
        toMCUCharacteristic = null
        fromMCUCharacteristic = null
    }

}