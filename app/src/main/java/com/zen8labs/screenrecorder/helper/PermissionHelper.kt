/******************************************************************************
 * Class : PermissionHelper.kt
 * Helper for handling permission
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.helper

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


class PermissionHelper {
    companion object {
        fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}