package com.hmj3908.andoridtoyproject.view.splash

import android.content.Intent
import com.hmj3908.andoridtoyproject.base.BaseContractActivity
import com.hmj3908.andoridtoyproject.databinding.ActSplashBinding
import com.hmj3908.andoridtoyproject.util.DataShareUtil
import com.hmj3908.andoridtoyproject.util.MethodStorage
import com.hmj3908.andoridtoyproject.view.main.ActMain

/**
 * Splash Activity
 */
class ActSplash : BaseContractActivity<ActSplashBinding>() {

    override fun getViewBinding() = ActSplashBinding.inflate(layoutInflater)

    override fun initData() {

        fullScreen = false // 풀 스크린
    }

    override fun initView() {

        // 너비 및 높이
        DataShareUtil.screenWidth = MethodStorage.getScreenWidth(this@ActSplash)
        DataShareUtil.screenHeight = MethodStorage.getScreenHeight(this@ActSplash)

        moveToMain()
    }

    /**
     * Main 이동
     */
    private fun moveToMain() {

        Intent(this, ActMain::class.java).apply {

            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(this)
        }
        finish()
    }
}