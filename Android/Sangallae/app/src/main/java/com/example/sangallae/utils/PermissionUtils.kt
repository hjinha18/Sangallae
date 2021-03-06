package com.example.sangallae.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import java.util.*

object PermissionUtils {
    fun requestPermission(
        activity: Activity, requestCode: Int, vararg permissions: String
    ): Boolean {
        var granted = true
        val permissionsNeeded = ArrayList<String>()
        for (s in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(activity, s)
            val hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED
            granted = granted and hasPermission
            if (!hasPermission) {
                permissionsNeeded.add(s)
            }
        }
        return if (granted) {
            true
        } else {
            requestPermissions(
                activity,
                permissionsNeeded.toTypedArray(),
                requestCode
            )
            false
        }
    }

    fun permissionGranted(
        requestCode: Int, permissionCode: Int, grantResults: IntArray
    ): Boolean {
        return requestCode == permissionCode && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}