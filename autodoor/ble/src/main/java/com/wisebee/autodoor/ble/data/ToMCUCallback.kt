package com.wisebee.autodoor.ble.data

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.callback.profile.ProfileReadResponse
import no.nordicsemi.android.ble.data.Data
import timber.log.Timber

abstract class ToMCUCallback: ProfileReadResponse() {

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
        Timber.tag("LedCallback").e("%d:%s", data.size(), byteArrayToHexString(data.value));
        /*
        if (data.size() == 1) {
            val ledState = data.getIntValue(Data.FORMAT_UINT8, 0) == 0x01
            onLedStateChanged(device, ledState)
        } else {
            onInvalidDataReceived(device, data)
        }
         */
    }

    abstract fun onLedStateChanged(device: BluetoothDevice, state: Boolean)
}