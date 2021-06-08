package com.rudderstack.android.sample.kotlin

import android.app.Application
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderConfig
import com.rudderstack.android.sdk.core.RudderLogger
import com.rudderstack.android.sdk.core.RudderOption
import com.rudderstack.generated.*

class MainApplication : Application() {
    companion object {
        var rudderClient: RudderClient? = null
        const val TAG = "MainApplication"
        const val DATA_PLANE_URL = "https://028ca7ca5687.ngrok.io"
        const val CONTROL_PLANE_URL = "https://0e741f50e567.ngrok.io"
        const val WRITE_KEY = "1pTxG1Tqxr7FCrqIy7j0p28AENV"
    }

    override fun onCreate() {
        super.onCreate()

//        rudderClient = RudderClient.getInstance(
//            this,
//            WRITE_KEY,
//            RudderConfig.Builder()
//                .withDataPlaneUrl(DATA_PLANE_URL)
//                .withLogLevel(RudderLogger.RudderLogLevel.DEBUG)
//                .withTrackLifecycleEvents(false)
//                .withRecordScreenViews(false)
//                .build(), RudderOption()
//                .putIntegration("MIXPANEL",true)
//        )
        rudderClient = RudderClient.getInstance(
            this,
            WRITE_KEY,
            RudderConfig.Builder()
                .withDataPlaneUrl(DATA_PLANE_URL)
                .withLogLevel(RudderLogger.RudderLogLevel.DEBUG)
                .withTrackLifecycleEvents(false)
                .withRecordScreenViews(false)
                .build()
        )
        rudderClient!!.putDeviceToken("some_device_token")
        rudderClient!!.track("first_event")

//        val twa = TypewriterAnalytics(rudderClient!!)
//        val option = RudderOption()
//        option.putIntegration("All", false)
//
//        // Dimensions Object
//        val dim = Dimensions.Builder().height(10.0).length(10.0).width(10.0).build()
//
//        // Sign In Failed Event
//        val sif =
//            SignInFailed.Builder().id("4008").numAttempts(1L).rememberMe(false).dimensions(dim)
//                .build()
//        twa.signInFailed(sif)
//        twa.signInFailed(sif, option)
//
//        // Sign In Submitted Event
//        val sis = SignInSubmitted.Builder().id("4009").numAttempts(21L).rememberMe(true).build()
//        twa.signInSubmitted(sis)
//        twa.signInSubmitted(sis, option)
//
//        // Sign In Succeeded Event
//        val sic = SignInSucceeded.Builder().id("4010").numAttempts(22L).rememberMe(false).build()
//        twa.signInSucceeded(sic)
//        twa.signInSucceeded(sic, option)
//
//        // User Signed out Event
//        val uso = UserSignedOut.Builder().id("4011").numAttempts(23L).rememberMe(true).build()
//        twa.userSignedOut(uso)
//        twa.userSignedOut(uso, option)

        Handler().postDelayed({
            RudderClient.updateWithAdvertisingId("some_idfa_changed")
            rudderClient!!.track("second_event")
        }, 3000)
    }
}
