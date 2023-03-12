package com.wisebee.autodoor.ble.data

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.callback.profile.ProfileReadResponse
import no.nordicsemi.android.ble.data.Data

abstract class FromMCUCallback: ProfileReadResponse() {

    open fun byteArrayToHexString(bytes: ByteArray?): String? {
        val builder = StringBuilder()
        if(bytes == null ) {
            return "null";
        }
        for (data in bytes) {
            builder.append(String.format("%02X ", data))
        }
        return builder.toString()
    }

    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        //Timber.tag("ButtonCallback").e("%d:%s", data.size(), byteArrayToHexString(data.value));

        //onDoorStateChanged(device, data.getIntValue(Data.FORMAT_UINT8, 2) ?: 0)
        onRxPacket(device, data.value!!)
        /*
        when(data.getByte(0)) {
            DataToMCU.FID_STATUS -> onDoorStateChanged(device, data.getIntValue(Data.FORMAT_UINT8, 2) ?: 0)
            else -> onRxPacket(device, data.value!!)
        }*/

        /*
        if (data.size() == 1) {
            val buttonState = data.getIntValue(Data.FORMAT_UINT8, 0) == 0x01
            onButtonStateChanged(device, buttonState)
        } else {
            onInvalidDataReceived(device, data)
        }*/
    }

    abstract fun onDoorStateChanged(device: BluetoothDevice, state: Int)

    abstract fun onRxPacket(device: BluetoothDevice, data: ByteArray)
}