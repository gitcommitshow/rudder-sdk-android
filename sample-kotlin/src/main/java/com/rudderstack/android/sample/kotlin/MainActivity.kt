package com.rudderstack.android.sample.kotlin

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.rudderstack.android.sdk.core.RudderOption
import com.rudderstack.android.sdk.core.RudderTraits
import com.rudderstack.generated.*


class MainActivity : AppCompatActivity() {
    private var count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed(Runnable {
//            MainApplication.rudderClient!!.track("Test Event 1")
//            MainApplication.rudderClient!!.track("Test Event 2")
//            MainApplication.rudderClient!!.track("Test Event 3")
//            MainApplication.rudderClient!!.track("Test Event 4")

            val twa = TypewriterAnalytics(MainApplication.rudderClient!!)
            val option = RudderOption()
            option.putIntegration("All", false)

            // Dimensions Object
            val dim = Dimensions.Builder().height(10.0).length(10.0).width(10.0).build()

            // Sign In Failed Event
            val sif =
                SignInFailed.Builder().id("4008").numAttempts(1L).rememberMe(false).dimensions(dim)
                    .build()
            twa.signInFailed(sif)
            twa.signInFailed(sif, option)

            // Sign In Submitted Event
            val sis = SignInSubmitted.Builder().id("4009").numAttempts(21L).rememberMe(true).build()
            twa.signInSubmitted(sis)
            twa.signInSubmitted(sis, option)

            // Sign In Succeeded Event
            val sic = SignInSucceeded.Builder().id("4010").numAttempts(22L).rememberMe(false).build()
            twa.signInSucceeded(sic)
            twa.signInSucceeded(sic, option)

            // User Signed out Event
            val uso = UserSignedOut.Builder().id("4011").numAttempts(23L).rememberMe(true).build()
            twa.userSignedOut(uso)
            twa.userSignedOut(uso, option)

            val option1 = RudderOption()
                    .putExternalId("brazeExternalId", "some_external_id_1")
                    .putExternalId("braze_id", "some_braze_id_2")
                    .putIntegration("GA", true).putIntegration("Amplitude", true)
                    .putCustomContext("customContext", mapOf("version" to "1.0.0", "language" to "kotlin"))
            MainApplication.rudderClient!!.identify(
                    "userId",
                    RudderTraits().putFirstName("Test First Name"),
                    option1
            )

            MainApplication.rudderClient!!.track("Test Event")
        }, 2000)
    }
}
