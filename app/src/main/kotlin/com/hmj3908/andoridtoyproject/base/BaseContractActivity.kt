package com.hmj3908.andoridtoyproject.base

import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.hmj3908.andoridtoyproject.util.MethodStorage

abstract class BaseContractActivity<B: ViewBinding> : BaseActivity<B>() {

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) setFullScreen()
    }

    /**
     * ViewModel Factory Lazy
     */
    inline fun <reified viewModel : ViewModel> AppCompatActivity.createViewModel(factory: ViewModelProvider.Factory? = null): viewModel {
        return if (factory == null) {
            ViewModelProvider(this)[viewModel::class.java]
        } else {
            ViewModelProvider(this, factory)[viewModel::class.java]
        }
    }

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

    /**
     * Hidden Keyboard
     */
    fun hideKeyboard(view: View?) {

        view?.let {

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}