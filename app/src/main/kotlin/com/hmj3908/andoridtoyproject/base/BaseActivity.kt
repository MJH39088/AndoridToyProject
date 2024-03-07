package com.hmj3908.andoridtoyproject.base

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.hmj3908.andoridtoyproject.R
import com.hmj3908.andoridtoyproject.util.CommonUtils
import io.reactivex.disposables.CompositeDisposable


abstract class BaseActivity<B: ViewBinding> : AppCompatActivity() {

    var _mBinding: B? = null
    protected val mBinding get() = _mBinding!!
    protected lateinit var mDisposable: CompositeDisposable

    var slideAnimation = false
    var verticalSlideAnimation = false
    var fullScreen = true

    var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mBinding = getViewBinding()
        setContentView(mBinding.root)
        initData()
        setFullScreen()
        initView()
        transparentStatusBar()
        screenOnOff(this@BaseActivity, false)
        screenOnOff(this@BaseActivity, true)

        // Slide 애니메이션 여부
        activityOpenAnimation()

        // 뒤로가기 콜백
        if ( backPressedCallback != null ) onBackPressedDispatcher.addCallback(this, backPressedCallback!!)
    }

    override fun finish() {
        super.finish()

        activityCloseAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun setContentView(layoutResID: Int) {
        layoutInflater.inflate(layoutResID, null).let {
            super.setContentView(it)
        }
    }

    /**
     * Activity Open Animation
     */
    private fun activityOpenAnimation() {

        if (slideAnimation) {
            overridePendingTransition(R.anim.anim_window_in, R.anim.anim_window_out)
        } else if (verticalSlideAnimation) {
            overridePendingTransition(
                R.anim.anim_window_vertical_in,
                R.anim.anim_window_vertical_out
            )
        } else {
            overridePendingTransition(0, 0)
        }
    }

    /**
     * Activity Close Animation
     */
    fun activityCloseAnimation() {

        if (slideAnimation) {
            overridePendingTransition(R.anim.anim_window_close_in, R.anim.anim_window_close_out)
        } else if (verticalSlideAnimation) {
            overridePendingTransition(
                R.anim.anim_window_vertical_close_in,
                R.anim.anim_window_vertical_close_out
            )
        } else {
            overridePendingTransition(0, 0)
        }
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {

        try {
            super.setRequestedOrientation(requestedOrientation)
        } catch (exception: Exception){
            exception.printStackTrace()
        }
    }

    abstract fun initData()
    abstract fun initView()
    abstract fun getViewBinding(): B

    /**
     * fullscreen
     */
    private fun setFullScreen() {

        if (!fullScreen) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp

            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        } else {

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun transparentStatusBar() {

        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        } else {
            window.setDecorFitsSystemWindows(false)
        }
    }

    /**
     * Display Keep Screen Flag
     */
    private fun screenOnOff(activity: Activity, screenOn: Boolean) {

        CommonUtils.log( "screenOn: $screenOn")
        if (screenOn) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}