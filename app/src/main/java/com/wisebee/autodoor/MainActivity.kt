package com.wisebee.autodoor

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import com.wisebee.autodoor.control.AutoDoorDestination
import com.wisebee.autodoor.scanner.ScannerDestination
import com.wisebee.autodoor.spec.AutoDoorSpec
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.common.navigation.NavigationView
import no.nordicsemi.android.common.theme.NordicActivity
import no.nordicsemi.android.common.theme.NordicTheme


@AndroidEntryPoint
class MainActivity: NordicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val versionStr = getVersion(this)
        val versions = versionStr.split('.')

        AutoDoorSpec.Companion.versionName = versionStr
        AutoDoorSpec.Companion.versionNum = 0
        for(i in 0..2) {
            if(i < versions.size)
                AutoDoorSpec.Companion.versionNum = AutoDoorSpec.Companion.versionNum * 100 + versions[i].toInt()
            else
                AutoDoorSpec.Companion.versionNum *= 100
        }

        setContent {
            NordicTheme {
                NavigationView(ScannerDestination + AutoDoorDestination)
            }
        }
    }

    private fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
        }

    private fun getVersion(context: Context): String {
        var versionName = ""
        try {
            val pInfo: PackageInfo =
                context.packageManager.getPackageInfoCompat(context.packageName, 0)
            versionName = pInfo.versionName + ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }
}