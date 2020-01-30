package com.rudderstack.android.sdk.core;

import com.google.gson.annotations.SerializedName;
import com.rudderstack.android.sdk.core.util.Utils;

class MetricsConfig {
    @SerializedName("enabled")
    private boolean isEnabled = Utils.METRICS_ENABLED;
    @SerializedName("statsConfigThreshold")
    private int statsConfigThreshold;
    @SerializedName("statsConfigInterval")
    private int statsConfigInterval;
    @SerializedName("writeKey")
    private String writeKey;
    @SerializedName("dataPlaneUrl")
    private String dataPlaneUrl;

    boolean isEnabled() {
        return isEnabled;
    }

    int getStatsConfigThreshold() {
        return statsConfigThreshold;
    }

    int getStatsConfigInterval() {
        return statsConfigInterval;
    }

    String getWriteKey() {
        return writeKey;
    }

    String getDataPlaneUrl() {
        return dataPlaneUrl;
    }
}
