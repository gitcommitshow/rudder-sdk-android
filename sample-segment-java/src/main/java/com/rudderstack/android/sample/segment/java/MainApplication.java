package com.rudderstack.android.sample.segment.java;

import android.app.Application;

import com.rudderstack.android.sdk.core.RudderClient;
import com.rudderstack.android.sdk.core.RudderConfig;
import com.rudderstack.android.sdk.core.RudderLogger;
import com.rudderstack.android.sdk.core.RudderMessageBuilder;
import com.rudderstack.android.sdk.core.RudderOption;

import com.rudderstack.generated.Dimensions;
import com.rudderstack.generated.SignInFailed;
import com.rudderstack.generated.SignInSubmitted;
import com.rudderstack.generated.SignInSucceeded;
import com.rudderstack.generated.TypewriterAnalytics;
import com.rudderstack.generated.UserSignedOut;

import java.util.HashMap;
import java.util.Map;

public class MainApplication extends Application {
    private static MainApplication instance;
    private static RudderClient rudderClient;

    private static final String END_POINT_URL = "https://028ca7ca5687.ngrok.io";
    private static final String WRITE_KEY = "1pTxG1Tqxr7FCrqIy7j0p28AENV";

    @Override
    public void onCreate() {
        super.onCreate();

        RudderConfig config = new RudderConfig.Builder()
                .withEndPointUri(END_POINT_URL)
                .withLogLevel(RudderLogger.RudderLogLevel.VERBOSE)
                .build();

        instance = this;

        rudderClient = new RudderClient.Builder(this, WRITE_KEY)
                .withRudderConfig(config)
                .build();

        RudderClient.with(this).onIntegrationReady("SOME_KEY", new RudderClient.Callback() {
            @Override
            public void onReady(Object instance) {

            }
        });

        RudderClient.setSingletonInstance(rudderClient);

        RudderClient client = RudderClient.getInstance(
                this,
                WRITE_KEY,
                new RudderConfig.Builder()
                        .withEndPointUri(END_POINT_URL)
                        .build()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("test_key_1", "test_value_1");
        Map<String, String> childProperties = new HashMap<>();
        childProperties.put("test_child_key_1", "test_child_value_1");
        properties.put("test_key_2", childProperties);
        if (rudderClient != null) {
            rudderClient.track(
                    new RudderMessageBuilder()
                            .setEventName("test_track_event")
                            .setUserId("test_user_id")
                            .setProperty(properties)
                            .build()
            );
        }
//        TypewriterAnalytics twa = new TypewriterAnalytics(rudderClient);
//
//
//        RudderOption option = new RudderOption();
//        option.putIntegration("All",false);
//
//        // Dimensions Object
//        Dimensions dim = new Dimensions.Builder().height(10d).length(10d).width(10d).build();
//
//        // Sign In Failed Event
//        SignInFailed sif = new SignInFailed.Builder().id("4008").numAttempts(1l).rememberMe(false).dimensions(dim).build();
//        twa.signInFailed(sif);
//        twa.signInFailed(sif,option);
//
//        // Sign In Submitted Event
//        SignInSubmitted sis = new SignInSubmitted.Builder().id("4009").numAttempts(21l).rememberMe(true).build();
//        twa.signInSubmitted(sis);
//        twa.signInSubmitted(sis,option);
//
//        // Sign In Succeeded Event
//        SignInSucceeded sic = new SignInSucceeded.Builder().id("4010").numAttempts(22l).rememberMe(false).build();
//        twa.signInSucceeded(sic);
//        twa.signInSucceeded(sic,option);
//
//        // User Signed out Event
//        UserSignedOut uso = new UserSignedOut.Builder().id("4011").numAttempts(23l).rememberMe(true).build();
//        twa.userSignedOut(uso);
//        twa.userSignedOut(uso,option);

    }

    public static RudderClient getRudderClient() {
        return rudderClient;
    }

    public static MainApplication getInstance() {
        return instance;
    }
}
