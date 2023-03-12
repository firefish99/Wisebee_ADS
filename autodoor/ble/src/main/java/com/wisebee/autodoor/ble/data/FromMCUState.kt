package com.wisebee.autodoor.ble.data

import android.bluetooth.BluetoothDevice

class FromMCUState: FromMCUCallback() {
    var state = 0
    var rxPacket = ByteArray(3)

    override fun onDoorStateChanged(device: BluetoothDevice, state: Int) {
        this.state = state
    }

    override fun onRxPacket(device: BluetoothDevice, data: ByteArray) {
        rxPacket = data
    }
}