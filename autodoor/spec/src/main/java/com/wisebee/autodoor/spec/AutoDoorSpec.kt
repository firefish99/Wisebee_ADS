package com.wisebee.autodoor.spec

import java.util.UUID

class AutoDoorSpec {

    companion object {
        val BLINKY_SERVICE_UUID: UUID = UUID.fromString("00001523-1312-efde-1523-785feabcd123")
        val BLINKY_BUTTON_CHARACTERISTIC_UUID: UUID = UUID.fromString("00001524-1312-efde-1523-785feabcd123")
        val BLINKY_LED_CHARACTERISTIC_UUID: UUID = UUID.fromString("00001525-1312-efde-1523-785feabcd123")
    }

}