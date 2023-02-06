package com.wisebee.autodoor.ble.data

import no.nordicsemi.android.ble.data.Data

class LedData private constructor() {

    companion object {
        fun from(value: Boolean): Data {
            //                     "0         1         2         3         4         5        6          7         8         9"
            //return Data.from("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789")
            return Data.from("0123456789012345678901234")
            //return Data.opCode(if (value) 0x01 else 0x00)
        }
    }

}