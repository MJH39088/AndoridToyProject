package com.hmj3908.andoridtoyproject.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.hmj3908.andoridtoyproject.BuildConfig
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CommonUtils {

    private var _context: Context? = null

    fun init(theContext: Context?): CommonUtils? {
        _context = theContext
        return _shared
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var _shared: CommonUtils? = null
        fun shared(): CommonUtils? {
            synchronized(CommonUtils::class.java) {
                if (_shared == null) {
                    _shared = CommonUtils()
                }
            }
            return _shared
        }

        /**
         * 디버깅
         *
         * @param _class
         */
        fun log(_class: Class<*>) {
            val name = _class.name
            var i = 0
            val size = name.split("\\.").toTypedArray().size
            var str = ""
            while (i < size) {
                str += name.split("\\.").toTypedArray()[i]
                if (i < size - 1) str += "/"
                i++
            }
            log("--------------------------------> $str")
        }

        fun log(vararg values: Any) {
            if (!BuildConfig.DEBUG) return
            for (obj in values) {
                if (obj is String) {
                    try {
                        var msg: String = obj
                        while (msg.isNotEmpty()) {
                            msg = if (msg.length > 4000) {
                                Log.d(">>>", msg.substring(0, 4000) + "")
                                msg.substring(4000)
                            } else {
                                Log.d(">>>", msg + "")
                                ""
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (obj is Int) {
                    try {
                        Log.d(">>>", obj.toString() + "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (obj is Float) {
                    try {
                        Log.d(">>>", obj.toString() + "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (obj is Double) {
                    try {
                        Log.d(">>>", obj.toString() + "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (obj is Boolean) {
                    try {
                        Log.d(">>>", obj.toString() + "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (obj is Class<*>) {
                    log(obj)
                }
            }
        }

        /**
         * SSL인증 무시
         * @return
         */
        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            return try {
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory
                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
                builder
            } catch (e: java.lang.Exception) {
                throw RuntimeException(e)
            }
        }

        /**
         * 스크린 사이즈
         * @param context
         * @return
         */
        @SuppressLint("NewApi", "ObsoleteSdkInt")
        fun screenSize(context: Context): Point {
            val result = Point()
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) display.getRealSize(
                result
            ) else display.getSize(result)
            return result
        }

        /**
         * DP/PX 변환
         *
         * @param context
         * @param px
         * @return
         */
        fun dpFromPx(context: Context, px: Float): Float {
            var scale = context.resources.displayMetrics.density
            scale = 3.0f
            return px / scale
        }

        /**
         * DP/PX 변환
         *
         * @param context
         * @param dp
         * @return
         */
        fun pxFromDp(context: Context, dp: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dp * scale
        }

        /**
         * Color
         * @param context
         * @param id
         * @return
         */
        fun getColor(context: Context, id: Int): Int {
            val version = Build.VERSION.SDK_INT
            return if (version >= 23) {
                ContextCompat.getColor(context, id)
            } else {
                context.resources.getColor(id)
            }
        }
    }
}