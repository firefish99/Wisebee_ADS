package com.wisebee.autodoor.di

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.wisebee.autodoor.ble.AutoDoorManager
import com.wisebee.autodoor.spec.AutoDoor
import com.wisebee.autodoor.spec.AutoDoorSpec
import com.wisebee.autodoor.spec.R
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Suppress("unused")
@Module
@InstallIn(ViewModelComponent::class)
abstract class AutoDoorModule {

    companion object {

        @Provides
        @ViewModelScoped
        fun provideBluetoothDevice(handle: SavedStateHandle): BluetoothDevice {
            //return handle.get(AutoDoor).device
            return AutoDoorSpec.bleDevice!!
        }

        @Provides
        @ViewModelScoped
        @Named("deviceName")
        fun provideDeviceName(
            @ApplicationContext context: Context,
            handle: SavedStateHandle,
        ): String {
            //return handle.get(AutoDoor).name ?: context.getString(R.string.unnamed_device)
            return AutoDoorSpec.bleName ?: context.getString(R.string.unnamed_device)
        }

        @Provides
        @ViewModelScoped
        @Named("deviceId")
        fun provideDeviceId(
            bluetoothDevice: BluetoothDevice
        ): String = bluetoothDevice.address

        @Provides
        @ViewModelScoped
        fun provideAutoDoorManager(
            @ApplicationContext context: Context,
            device: BluetoothDevice,
        ) = AutoDoorManager(context, device)

    }

    @Binds
    abstract fun bindAutoDoor(
        AdsManager: AutoDoorManager
    ): AutoDoor

}