package com.wisebee.autodoor.di

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import com.wisebee.autodoor.ble.AutoDoorManager
import com.wisebee.autodoor.control.Blinky
import com.wisebee.autodoor.spec.AutoDoor
import com.wisebee.autodoor.spec.R
import no.nordicsemi.android.common.navigation.get
import javax.inject.Named

@Suppress("unused")
@Module
@InstallIn(ViewModelComponent::class)
abstract class AutoDoorModule {

    companion object {

        @Provides
        @ViewModelScoped
        fun provideBluetoothDevice(handle: SavedStateHandle): BluetoothDevice {
            return handle.get(Blinky).device
        }

        @Provides
        @ViewModelScoped
        @Named("deviceName")
        fun provideDeviceName(
            @ApplicationContext context: Context,
            handle: SavedStateHandle,
        ): String {
            return handle.get(Blinky).name ?: context.getString(R.string.unnamed_device)
        }

        @Provides
        @ViewModelScoped
        @Named("deviceId")
        fun provideDeviceId(
            bluetoothDevice: BluetoothDevice
        ): String = bluetoothDevice.address

        @Provides
        @ViewModelScoped
        fun provideBlinkyManager(
            @ApplicationContext context: Context,
            device: BluetoothDevice,
        ) = AutoDoorManager(context, device)

    }

    @Binds
    abstract fun bindBlinky(
        BlinkManager: AutoDoorManager
    ): AutoDoor

}