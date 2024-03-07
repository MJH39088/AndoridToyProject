package com.hmj3908.andoridtoyproject.util

import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import java.util.Locale

class DataShareUtil {

    // RTL 여부
    val isRtl by lazy {

        TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    companion object {

        var screenWidth = 0              // 스크린 너비
        var screenHeight = 0             // 스크린 높이

        @Volatile private var instance: DataShareUtil? = null
        @JvmStatic fun getInstance(): DataShareUtil =
            instance ?: synchronized(this) {
                instance ?: DataShareUtil().also {
                    instance = it
                }
            }
    }
}