package com.hmj3908.andoridtoyproject.util

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * Permission Util
 */
object PermissionUtil {

    /**
     * 필요한 모든 권한이 허용되었는지 확인하는 함수
     * @param context Context 객체
     * @return 모든 권한이 허용되었는지의 여부를 반환
     */
    fun checkPermissions(context: Context): Boolean {
        return checkPermissionNotification(context) && checkPermissionOverlay(context)
                && checkPermissionUsageStats(context) && checkPermissionStorage(context)
    }

    /**
     * 알림 권한이 허용되었는지 확인하는 함수
     * @param context Context 객체
     * @return 권한이 허용되었는지의 여부 반환
     */
    fun checkPermissionNotification(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * 다른 앱 위에 표시 권한이 허용되었는지 확인하는 함수
     * @param context Context 객체
     * @return 권한이 허용되었는지의 여부를 반환
     */
    fun checkPermissionOverlay(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * 사용자 앱 사용 기록 접근 권한이 허용되었는지 확인하는 함수
     * @param context Context 객체
     * @return 권한이 허용되었는지의 여부 반환
     */
    fun checkPermissionUsageStats(context: Context): Boolean {

        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            when (val opMode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)) {

                AppOpsManager.MODE_DEFAULT -> (PackageManager.PERMISSION_GRANTED
                        == context.checkPermission(Manifest.permission.PACKAGE_USAGE_STATS, Process.myPid(), context.applicationInfo.uid))

                AppOpsManager.MODE_ALLOWED -> true
                AppOpsManager.MODE_ERRORED, AppOpsManager.MODE_IGNORED -> false
                else -> throw IllegalStateException("Unknown AppOpsManager mode $opMode")
            }
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 외부 저장소 접근 권한이 허용되었는지 확인하는 함수
     * @param context Context 객체
     * @return 권한이 허용되었는지의 여부 반환
     */
    fun checkPermissionStorage(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 알림 권한을 요청하는 함수
     * @param activity Activity 객체
     * @param launcher 결과를 처리할 ActivityResultLauncher 객체
     */
    fun requestPermissionNotification(activity: Activity, launcher: ActivityResultLauncher<String>, deniedLauncher: ActivityResultLauncher<Intent>) {
        if (!checkPermissionNotification(activity)) {
            if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {

                // 권한을 거부한 경우, 설정으로 이동
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse(String.format("package:%s", activity.packageName))
                deniedLauncher.launch(intent)
            }
        }
    }

    /**
     * 다른 앱 위에 표시 권한을 요청하는 함수
     * @param activity Activity 객체
     * @param launcher 결과를 처리할 ActivityResultLauncher 객체
     */
    fun requestOverlayPermission(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
        if (!checkPermissionOverlay(activity)) {

            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
            intent.data = Uri.parse(String.format("package:%s", activity.packageName))
            launcher.launch(intent)
        }
    }

    /**
     * 사용자 앱 사용 기록 접근 권한 요청하는 함수
     * @param activity Activity 객체
     * @param launcher 결과를 처리할 ActivityResultLauncher 객체
     */
    fun requestPermissionUsageStats(activity: Activity, launcher: ActivityResultLauncher<Intent>) {

        try {

            val intent = Intent()
            intent.action = Settings.ACTION_USAGE_ACCESS_SETTINGS
            intent.data = Uri.parse(String.format("package:%s", activity.packageName))
            launcher.launch(intent)

        } catch (e: Exception) {

            val intent = Intent()
            intent.action = Settings.ACTION_USAGE_ACCESS_SETTINGS
            launcher.launch(intent)
        }
    }

    /**
     * 외부 저장소 접근 권한 요청하는 함수
     * @param activity Activity 객체
     * @param launcher 결과를 처리할 ActivityResultLauncher 객체
     */
    fun requestPermissionStorage(activity: Activity, launcher: ActivityResultLauncher<Intent>, multipleLauncher: ActivityResultLauncher<Array<String>>) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            try {

                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", activity.packageName))
                launcher.launch(intent)

            } catch (e: Exception) {

                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                launcher.launch(intent)
            }

        } else {

            multipleLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }
}