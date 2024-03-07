@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.parcelize)
}

android {

    namespace = "com.hmj3908.andoridtoyproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hmj3908.andoridtoyproject"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    applicationVariants.all {

        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "test-app-v${versionName}.apk"
                println("OutputFileName: $outputFileName")
                output.outputFileName = outputFileName
            }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    lint {

        abortOnError = false
        checkReleaseBuilds = false
        htmlReport = true
        htmlOutput = file("$project.buildDir/reports/lint/lint.html")
        disable.add("MissingTranslation")
        disable.add("GradleDependency")
        disable.add("VectorPath")
        disable.add("IconMissingDensityFolder")
        disable.add("IconDensities")
    }

    packaging {

        resources.excludes.add("META-INF/*")
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/ASL2.0")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/services/javax.annotation.processing.Processor")
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }
}

dependencies {

    /** Android */
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /** Splash */
    implementation(libs.androidx.core.splashscreen)

    /** lifeCycle */
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    /** Media 3 */
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer.dash)

    /** Retrofit */
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.adapter.rxjava2)

    /** OkHttp */
    implementation(libs.okhttp)
    implementation(libs.okhttp.urlconnection)
    implementation(libs.logging.interceptor)

    /** moshi */
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin.codegen)

    /** Gson */
    implementation(libs.gson)

    /** RxJava3 */
    implementation(libs.rxjava)
    implementation(libs.rxkotlin)
    implementation(libs.rxandroid)

    /** RxJava2 */
    implementation(libs.rxjava2.rxjava)
    implementation(libs.rxjava2.rxandroid)

    /** Coroutine Core */
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    /** Coil */
    implementation(libs.coil)

    /** Data Store */
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    /** WorkManager */
    implementation(libs.androidx.work.runtime.ktx)

    /** Hilt */
    implementation(libs.hilt.android)
    implementation(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.hilt.compiler)

    /** Glide */
    implementation(libs.glide)
    implementation(libs.glide.transformations)

    /** Rive */
    implementation(libs.rive.android)
    implementation(libs.androidx.startup.runtime)

    /** Amazon analytics sdk for android */
    implementation(libs.aws.android.sdk.mobileanalytics)
    implementation(libs.aws.android.sdk.mobile.client)
    implementation(libs.aws.android.sdk.pinpoint)
    implementation(libs.aws.android.sdk.cognito)

    /** View ScreenShot */
    implementation(libs.screenshotty)
    implementation(libs.screenshotty.rx)

    /** Lottie Aniamtion */
    implementation(libs.lottie)

    /** Balloon */
    implementation(libs.balloon)

    /** Screen Size */
    implementation(libs.screen.easy)

    /** Blur */
    implementation (libs.realtimeblurview)
}