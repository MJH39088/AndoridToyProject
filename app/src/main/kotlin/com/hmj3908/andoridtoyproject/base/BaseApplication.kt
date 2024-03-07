package com.hmj3908.andoridtoyproject.base

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.AppInitializer
import androidx.work.Configuration
import app.rive.runtime.kotlin.RiveInitializer
import com.hmj3908.andoridtoyproject.util.PreferencesUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        initializeService()
    }

    /**
     * 서비스 설정
     */
    private fun initializeService() {

        applicationContext?.let {

            /**
             * SharedPreferences 유틸 초기화
             */
            PreferencesUtil.context = it

            // Rive 초기화
            AppInitializer.getInstance(applicationContext).initializeComponent(RiveInitializer::class.java)
        }
    }
}