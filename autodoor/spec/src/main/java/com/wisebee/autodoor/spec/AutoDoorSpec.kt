package com.wisebee.autodoor.spec

import android.bluetooth.BluetoothDevice
import java.util.*

class AutoDoorSpec {

    companion object {
        val AUTODOOR_SERVICE_UUID: UUID = UUID.fromString("00001523-1312-efde-1523-785feabcd123")
        val AUTODOOR_BUTTON_CHARACTERISTIC_UUID: UUID = UUID.fromString("00001524-1312-efde-1523-785feabcd123")
        val AUTODOOR_LED_CHARACTERISTIC_UUID: UUID = UUID.fromString("00001525-1312-efde-1523-785feabcd123")

        var bleDevice : BluetoothDevice? = null
        var bleName : String? = null
        var versionName = "1.0.0"
        var versionNum = 0
    }

}