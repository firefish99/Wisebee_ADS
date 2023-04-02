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
        const val FID_APP_SYS_COMMAND : Byte = 0x0C
        const val FID_APP_BLE_COMMAND : Byte = 0x0D
        const val FID_APP_RW_BLOCK : Byte = 0x0E

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
        const val CALIB_ERR_NONE : Int = 2
        const val CALIB_ERR_INIT : Int = 3
        const val CALIB_ERR_OTHER : Int = 4
        const val CALIB_ERR_WAIT : Int = 5
        const val CALIB_ERR_SENSOR : Int = 6
        const val CALIB_ERR_DOING : Int = 7
        const val CALIB_ERR_ERROR : Int = 8
        const val CALIB_ERR_STOP : Int = 9
        const val CALIB_OK_ING : Int = 10
        const val CALIB_OK_DONE : Int = 11

        //FID_APP_TEST_MODE
        const val CMD_GET_TEST_MODE : Byte = 0x00
        const val CMD_SET_TEST_MODE : Byte = 0x01
        const val CMD_START_TEST_MODE : Byte = 0x02
        const val CMD_STOP_TEST_MODE : Byte = 0x03
        const val CMD_CRASH_TEST_MODE : Byte = 0x04
        const val TMODE_ERR_NONE : Int = 2
        const val TMODE_ERR_STAT : Int = 3
        const val TMODE_ERR_ERROR : Int = 4
        const val TMODE_ERR_TMODE : Int = 5
        const val TMODE_OK_ING : Int = 6
        const val TMODE_OK_DONE : Int = 7

        //FID_APP_SYS_COMMAND
        const val CMD_UPDATE_USB : Byte = 0x01
        const val CMD_UPDATE_SFLASH : Byte = 0x02
        const val CMD_SET_DEFAULT : Byte = 0x03
        const val CMD_INIT_OPER_STAT : Byte = 0x04
        const val BLE_SAVE_SUBCONFIG : Byte = 0x05
        const val BLE_LOAD_SUBCONFIG : Byte = 0x06
        const val SYS_CMD_SUCCESS : Int = 1
        const val SYS_CMD_NO_BIN : Int = 2

        //FID_APP_BLE_COMMAND
        const val CMD_GET_BLE_STATUS : Byte = 0x00
        const val CMD_CLEAR_BUTTON_MAC : Byte = 0x01
        const val CMD_RESET_BLE_MODULE : Byte = 0x02

        //FID_APP_RW_BLOCK
        const val BLE_RW_INITIAL_CMD : Byte = 0x01
        const val BLE_RW_SENSOR_CMD : Byte = 0x02
        const val BLE_RW_TOF1_CMD : Byte = 0x03
        const val BLE_RW_TOF2_CMD : Byte = 0x04
        const val BLE_RW_RADAR_CMD : Byte = 0x05
        const val BLE_RW_DCM_CMD : Byte = 0x06
        const val BLE_RW_BLDC_CMD : Byte = 0x07
        const val BLE_RW_HLED_CMD : Byte = 0x08
        const val BLE_RW_CMD_MASK : Byte = 0x3f
        const val BLE_RW_READ : Byte = 0x00
        const val BLE_RW_WRITE : Byte = 0x40

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
        fun sendCommand(fid: Byte): Data {
            return Data(byteArrayOf(fid, 0x02))
        }

        fun doorCmd(cmd: Byte): Data {
            return Data(byteArrayOf(FID_APP_COMMAND, 0x03, cmd))
        }

        fun sendCommand(fid: Byte, flag: Byte): Data {
            return Data(byteArrayOf(fid, 0x03, flag))
        }

        fun sendCommand(fid: Byte, data: ByteArray): Data {
            return Data(byteArrayOf(fid, (2 + data.size).toByte(), *data))
        }
    }

}