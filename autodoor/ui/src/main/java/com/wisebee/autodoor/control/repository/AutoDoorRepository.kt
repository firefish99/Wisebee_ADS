package com.wisebee.autodoor.control.repository

import android.content.Context
import android.net.Uri
import com.wisebee.autodoor.spec.AutoDoor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.log.timber.nRFLoggerTree
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 *
 * @param context The application context.
 * @param deviceId The device ID.
 * @param deviceName The name of the Blinky device, as advertised.
 * @property autoDoor The AutoDoor implementation.
 */
class AutoDoorRepository @Inject constructor(
    @ApplicationContext context: Context,
    @Named("deviceId") deviceId: String,
    @Named("deviceName") deviceName: String,
    private val autoDoor: AutoDoor,
): AutoDoor by autoDoor {
    /** Timber tree that logs to nRF Logger. */
    private val tree: Timber.Tree

    /** If the nRF Logger is installed, this URI will allow to open the session. */
    internal val sessionUri: Uri?

    init {
        // Plant a new Tree that logs to nRF Logger.
        tree = nRFLoggerTree(context, null, deviceId, deviceName)
            .also { Timber.plant(it) }
            .also { sessionUri = it.session?.sessionUri }
    }

    val loggedDisplayView: Flow<AutoDoor.DisplayView>
        get() = autoDoor.displayView

    val loggedRxPacket: StateFlow<ByteArray>
        get() = autoDoor.rxPacket

    override fun release() {
        Timber.uproot(tree)
        autoDoor.release()
    }
}