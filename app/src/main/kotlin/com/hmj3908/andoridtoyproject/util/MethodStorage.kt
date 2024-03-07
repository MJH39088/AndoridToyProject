package com.hmj3908.andoridtoyproject.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Insets
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Locale


class MethodStorage {

    companion object {

        /**
         * 이미지 스크린너비에 맞추기
         */
        fun setImageResize(view: View?) {

            view?.let {

                // 스크린 너비 맞추기
                val screenWidth = DataShareUtil.screenWidth

                val originWidth = view.width.toFloat()
                val originHeight = view.height.toFloat()
                val newHeight = screenWidth * originHeight / originWidth

                val layoutParams = view.layoutParams
                layoutParams.width = screenWidth.toInt()
                layoutParams.height = newHeight.toInt()

                view.layoutParams = layoutParams
            }
        }

        /**
         * getScreenHeight
         */
        fun getScreenHeight(activity: Activity): Int {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                val windowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.systemBars()
                )
                windowMetrics.bounds.height() - insets.top - insets.bottom

            } else {

                val displayMetrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.heightPixels
            }
        }

        /**
         * getScreenWidth
         */
        fun getScreenWidth(activity: Activity): Int {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                val windowMetrics = activity.windowManager.currentWindowMetrics
                val insets =
                    windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                windowMetrics.bounds.width() - insets.left - insets.right

            } else {

                val displayMetrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.widthPixels
            }
        }

        /**
         * Status Bar 색상 변경
         */
        fun setStatusBarColor(activity: Activity?, color: Int) {

            activity?.let {

                val window: Window = it.window

                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                // finally change the color
                window.statusBarColor = ContextCompat.getColor(it, color)
            }
        }

        /**
         * READ VIDEO
         * @param file File
         * @return uri
         */
        fun readVideoUri(file: File): Uri? {

            return if (file.exists()) {
                Uri.fromFile(file)
            } else {
                null
            }
        }

        @SuppressLint("CheckResult")
        fun rxJavaSetImage(imageView: ImageView, bitmap: Bitmap?, completeCallback: (() -> Unit)) {

            if (imageView.context == null) {
                CommonUtils.log("imageView context is null")
                return
            }

            if (bitmap == null) {
                CommonUtils.log("Bitmap is null")
                return
            }

            Observable.create { emitter: ObservableEmitter<Drawable> ->
                try {
                    val drawable = Glide.with(imageView.context)
                        .load(bitmap)
                        .override(imageView.width, imageView.height)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .submit()
                        .get()

                    emitter.onNext(drawable)
                    emitter.onComplete()
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { drawable ->
                        imageView.setImageDrawable(drawable)
                        imageView.visibility = View.VISIBLE
                        completeCallback.invoke()
                    },
                    { error ->
                        error.printStackTrace()
                    }
                )
        }

        /**
         * 이미지 출력
         *
         * @param imageView
         * @param file
         */
        fun setImage(imageView: ImageView, file: File) {

            if (imageView.context == null) return

            try {

                Glide.with(imageView.context)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(imageView.width, imageView.height)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {

                            imageView.setImageDrawable(resource)
                        }
                    })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 이미지 출력
         *
         * @param imageView
         * @param bitmap
         */
        fun setGlideImage(image: AppCompatImageView, bitmap: Bitmap?, glide: RequestManager) {

            val placeholderDrawable = BitmapDrawable(image.resources, bitmap)

            glide.asBitmap()
                .load(bitmap)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .placeholder(placeholderDrawable)
                .override(image.width, image.height)
                .into(image)
        }

        /**
         * 이미지 출력 GIF
         */
        fun setGIF(ctx: Context?, glide: RequestManager, imageFile: File?, preview: ImageView, gifView: ImageView) {

            if (ctx == null) return

            imageFile?.let { file ->

                val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(gifView.width, gifView.height)
                    .skipMemoryCache(true)

                glide
                    .asGif()
                    .load(file)
                    .apply(options)
                    .listener(object : RequestListener<GifDrawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<GifDrawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: GifDrawable,
                            model: Any,
                            target: Target<GifDrawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {

                            preview.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(object : CustomViewTarget<ImageView, GifDrawable>(gifView) {

                        override fun onResourceReady(
                            resource: GifDrawable,
                            transition: Transition<in GifDrawable>?
                        ) {

                            preview.visibility = View.GONE
                            resource.start()
                            gifView.setImageDrawable(resource)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {}
                        override fun onResourceCleared(placeholder: Drawable?) {}
                    })
            }
        }

        /**
         * 디바이스 언어 설정 가져오기
         */
        private fun getSystemLanguage(): String = Locale.getDefault().language

        /**
         * 디바이스 국가 설정 가져오기
         */
        fun getSystemCountry(): String = Locale.getDefault().country

        /**
         * 앱 설치 여부 확인
         */
        fun isAppInstalled(ctx: Context, packageName: String): Boolean {

            val packageManager = ctx.packageManager
            return try {
                packageManager.getApplicationInfo(packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 클릭 시 해당 앱 이동
         */
        fun moveToApp(context: Context, pkg: String) {

            val options = Bundle().apply {}

            try {

                context.startActivity(context.packageManager.getLaunchIntentForPackage(pkg), options)
            } catch (e: NullPointerException) {
                if (pkg.isEmpty()) {
                    Toast.makeText(context, "패키지명이 없습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "not installed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * isRtl textView Alignment
         */
        fun isRtlAlignment(view: View) {

            val isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
            view.layoutDirection = if (isRtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
            view.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }

        /**
         * isRtl Assets
         * @param view -> View
         */
        fun isRtlAssets(view: View) {

            val isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
            view.apply {

                rotation = if (isRtl) { 180f } else { 0f }
            }
        }

        /**
         * File to ByteArray
         * @param file
         */
        fun fileToByteArray(file: File): ByteArray? {

            try {

                val inputStream = FileInputStream(file)
                val outputStream = ByteArrayOutputStream()

                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                inputStream.close()

                return outputStream.toByteArray()

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * bitmap to ByteArray
         * @param bitmap
         */
        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return stream.toByteArray()
        }

        /**
         * 이미지 가져오는 함수
         */
        fun getImage(context: Context, imageByteArray: ByteArray?): Drawable? {

            return imageByteArray?.let { BitmapFactory.decodeByteArray(it, 0, it.size).toDrawable(context.resources) }
        }

        /**
         * NetWork 연결 여부
         *
         * @return [Boolean]
         */
        fun isNetworkAvailable(context: Context): Boolean {

            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } else {

                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION")
                return networkInfo.isConnected
            }
        }
    }
}