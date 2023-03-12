package com.wisebee.autodoor.ble.data

import no.nordicsemi.android.ble.data.Data

class DataToMCU private constructor() {

    companion object {
        const val FID_APP_STATUS : Byte = 0x00
        const val FID_APP_COMMAND : Byte = 0x01
        const val FID_APP_OPER_STAT : Byte = 0x02
        const val FID_APP_VERSION : Byte = 0x03
        const val FID_APP_CHANGE_MODE : Byte = 0x04
        const val FID_APP_RENAME_DEVICE : Byte = 0x05
        const val FID_APP_USER_PARAM : Byte = 0x06
        const val FID_APP_CHANGE_PW : Byte = 0x07
        const val FID_APP_AUTH_USER_PW : Byte = 0x08
        const val FID_APP_AUTH_ADMIN_PW : Byte = 0x09
        const val FID_APP_SENSOR_CALIB : Byte = 0x0A
        const val FID_APP_TEST_MODE : Byte = 0x0B

        const val FID_APP_NONE : Byte = 0x40

        // FID_APP_STATUS
        const val STS_OPEN_DOOR : Byte = 0x01
        const val STS_CLOSE_HOLD : Byte = 0x02
        const val STS_OPEN_HOLD : Byte = 0x03
        const val STS_HALF_OPEN : Byte = 0x04
        const val STS_EM_STOP : Byte = 0x05

        // FID_APP_COMMAND
        const val CMD_OPEN_DOOR : Byte = 0x01
        const val CMD_CLOSE_HOLD : Byte = 0x02
        const val CMD_OPEN_HOLD : Byte = 0x03
        const val CMD_HALF_OPEN : Byte = 0x04
        const val CMD_EM_STOP : Byte = 0x05
        const val CMD_UPDATE_USB : Byte = 0x10
        const val CMD_UPDATE_SFLASH : Byte = 0x11
        const val CMD_SET_DEFAULT : Byte = 0x12
        const val CMD_INIT_OPER_STAT : Byte = 0x13

        //FID_APP_CHANGE_MODE
        const val CMD_GET_MODE : Byte = 0x00
        const val CMD_CHANGE_MODE : Byte = 0x01

        //FID_APP_RENAME_DEVICE
        const val CMD_GET_NAME : Byte = 0x00
        const val CMD_CHANGE_NAME : Byte = 0x01

        //FID_APP_USER_PARAM
        const val CMD_GET_USER_PARAM : Byte = 0x00
        const val CMD_SET_USER_PARAM : Byte = 0x01      // 0x01 ~ 0x06

        //FID_APP_SENSOR_CALIB
        const val CMD_GET_CALIB : Byte = 0x00
        const val CMD_SET_CALIB : Byte = 0x01
        const val ID_CALIB_MOTOR : Byte = 0x01
        const val ID_CALIB_ALL : Byte = 0x02
        const val ID_CALIB_TOF1 : Byte = 0x03
        const val ID_CALIB_TOF2 : Byte = 0x04
        const val ID_CALIB_RADAR : Byte = 0x05

        //FID_APP_TEST_MODE
        const val CMD_GET_TEST_MODE : Byte = 0x00
        const val CMD_SET_TEST_MODE : Byte = 0x01
        const val CMD_START_TEST_MODE : Byte = 0x02
        const val CMD_STOP_TEST_MODE : Byte = 0x03

        fun from(value: Boolean): Data {
            //                     "0         1         2         3         4         5        6          7         8         9"
            //return Data.from("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789")
            return Data.from("0123456789012345678901234")
            //return Data.opCode(if (value) 0x01 else 0x00)
        }

        /*
         * FID = 1 byte
         * Length = 1 byte, (FID + LENGTH + ...)
         * Data = n byte
         */
        fun getStatus(fid: Byte): Data {
            return Data(byteArrayOf(fid, 0x02))
        }

        fun getStatus(fid: Byte, flag: Byte): Data {
            return Data(byteArrayOf(fid, 0x03, flag))
        }

        fun doorCmd(cmd: Byte): Data {
            return Data(byteArrayOf(FID_APP_COMMAND, 0x03, cmd))
        }

        fun sendCommand(fid: Byte, data: ByteArray): Data {
            return Data(byteArrayOf(fid, (2 + data.size).toByte(), *data))
        }
    }

}