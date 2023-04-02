package com.wisebee.autodoor.spec

import kotlinx.coroutines.flow.StateFlow

interface AutoDoor {

    enum class State {
        LOADING,
        READY,
        NOT_AVAILABLE
    }

    enum class AuthorizedPw {
        PREPARE,
        REQUESTED,
        SUCCESS,
        FAIL
    }

    enum class DisplayView {
        VIEW_MAIN,
        VIEW_USER_MODE,
        VIEW_VERSION,
        VIEW_OPER_STAT,
        VIEW_CHANGE_MODE,
        VIEW_RENAME_DEVICE,
        VIEW_USER_PARAM,
        VIEW_CHANGE_PW,
        VIEW_ADMIN_AUTH,
        VIEW_ADMIN_MODE,
        VIEW_ADMIN_PARAM,
        VIEW_CALIBRATION,
        VIEW_UPDATE,
        VIEW_OPER_INIT,
        VIEW_TEST_MODE,
        VIEW_INITIAL_TIME,
        VIEW_SENSOR_ENABLE,
        VIEW_TOF1,
        VIEW_TOF2,
        VIEW_RADAR,
        VIEW_MAIN_BLE,
        VIEW_DCM,
        VIEW_BLDC,
        VIEW_HLED,
    }

    suspend fun connect()

    fun release()

    val state: StateFlow<State>

    val displayView: StateFlow<DisplayView>
    fun setDisplay(view: DisplayView)

    val rxPacket: StateFlow<ByteArray>

    suspend fun commandDoor(cmd: Byte)
    suspend fun sendCommand(fid: Byte, data: ByteArray)
    suspend fun sendCommand(fid: Byte)
    suspend fun sendCommand(fid: Byte, flag:Byte)
}