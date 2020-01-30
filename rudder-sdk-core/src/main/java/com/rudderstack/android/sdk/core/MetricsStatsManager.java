package com.rudderstack.android.sdk.core;

import android.app.Application;
import android.text.TextUtils;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.rudderstack.android.sdk.core.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_CONFIG_PLANE_FIELD_RESPONSE_TIME;
import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_CONFIG_PLANE_FIELD_SUCCESS;
import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_CONFIG_PLANE_TABLE_NAME;
import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_DATA_PLANE_FIELD_RESPONSE_TIME;
import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_DATA_PLANE_FIELD_SIZE;
import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_DATA_PLANE_FIELD_SUCCESS;
import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_DATA_PLANE_TABLE_NAME;
import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_EVENT_FIELD_SIZE;
import static com.rudderstack.android.sdk.core.DBPersistentManager.METRICS_EVENT_TABLE_NAME;

class MetricsStatsManager {
    private static MetricsStatsManager instance;
    private DBPersistentManager dbPersistentManager;
    private RudderPreferenceManager preferenceManager;
    private RudderHttpClient rudderHttpClient;
    private static volatile MetricsConfig metricsConfig;

    private MetricsStatsManager(Application application) {
        rudderHttpClient = RudderHttpClient.getInstance();
        RudderLogger.logVerbose("MetricsStatsManager: creating db persistent manager");
        this.dbPersistentManager = DBPersistentManager.getInstance(application);
        RudderLogger.logVerbose("MetricsStatsManager: creating preference manager");
        this.preferenceManager = RudderPreferenceManager.getInstance(application);
        // check and set the begin time if not set.
        this.preferenceManager.getStatsBeginTime();

        RudderLogger.logVerbose("MetricsStatsManager: creating scheduled executor service");
        new Thread(getWorkerRunnable()).start();
    }

    private Runnable getWorkerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                int sleepCount = 0;
                while (isEnabled()) {
                    if (metricsConfig == null) {
                        downloadConfigJson();
                        if (metricsConfig == null) {
                            parsePersistedConfig();
                        }
                    }
                    if (metricsConfig != null) {
                        if (metricsConfig.isEnabled()) {
                            int maxRecordCount = dbPersistentManager.getMaxStatsRecordCount();
                            if (maxRecordCount >= metricsConfig.getStatsConfigThreshold() || (maxRecordCount > 0 && sleepCount >= metricsConfig.getStatsConfigInterval())) {
                                fetchLastMetrics();
                            }
                            flushRequestsToServer();
                        } else {
                            RudderLogger.logVerbose("MetricsStatsManager: logging is not enabled. shutting down metrics logger");
                            dbPersistentManager.deleteAllMetrics();
                            dbPersistentManager.deleteAllMetricsRequest();
                        }
                    }
                    try {
                        sleepCount += 1;
                        Thread.sleep(Utils.STATS_DELAY_TIME_UNIT.toMillis(Utils.STATS_DELAY_COUNT));
                    } catch (Exception e) {
                        RudderLogger.logError(e);
                    }
                }
            }
        };
    }

    private void fetchLastMetrics() {
        boolean logRequest = false;
        Map<String, Object> params = new HashMap<>();
        for (MetricsStats stats : MetricsStats.values()) {
            List<Integer> list = this.dbPersistentManager.getStats(stats.getQuerySql());
            if (list != null && !list.isEmpty()) {
                logRequest = true;
                params.put("pr_" + stats.getParamPrefix(), list);
            }
        }
        int retryCountConfigPlane = dbPersistentManager.getRetryCountConfigPlane();
        if (retryCountConfigPlane > 0) {
            logRequest = true;
            params.put("pr_retryCountConfigPlane", String.valueOf(retryCountConfigPlane));
        }
        int retryCountDataPlane = dbPersistentManager.getRetryCountDataPlane();
        if (retryCountDataPlane > 0) {
            logRequest = true;
            params.put("pr_retryCountDataPlane", String.valueOf(retryCountDataPlane));
        }

        if (logRequest) {
            params.put("writeKey", metricsConfig.getWriteKey());
            RudderContext context = RudderElementCache.getCachedContext();
            params.put("c_os", context.getOsInfo().getName());
            params.put("c_version", context.getOsInfo().getVersion());
            params.put("c_sdk", context.getLibraryInfo().getVersion());
            params.put("pr_begin", String.valueOf(preferenceManager.getStatsBeginTime()));
            long statsEndTime = System.currentTimeMillis();
            params.put("pr_end", String.valueOf(statsEndTime));
            params.put("c_fingerprint", preferenceManager.getRudderStatsFingerPrint());
            dbPersistentManager.saveStatsRequest(String.format("%s?data=%s", metricsConfig.getDataPlaneUrl(), new Gson().toJson(params)));
            dbPersistentManager.deleteAllMetrics();
            preferenceManager.updateRudderStatsBeginTime(System.currentTimeMillis());
        }
    }

    private void flushRequestsToServer() {
        // do not send metrics if it is not configured in server config
        if (metricsConfig != null && metricsConfig.isEnabled()) {
            SparseArray<String> requests = dbPersistentManager.getMetricsRequests();
            if (requests.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (int index = 0; index < requests.size(); index++) {
                    String response = rudderHttpClient.get(String.format(Locale.US, "%s&timestamp=%d", requests.valueAt(index), System.currentTimeMillis()), null);
                    if (response.equalsIgnoreCase("OK")) {
                        builder.append(requests.keyAt(index));
                    }
                    builder.append(",");
                }
                // remove last "," character
                builder.deleteCharAt(builder.length() - 1);
                dbPersistentManager.clearMetricsRequestFromDB(builder.toString());
            }
        }
    }

    private void parsePersistedConfig() {
        this.parseConfigJson(preferenceManager.getStatsConfigJson());
    }

    private void downloadConfigJson() {
        String configUrl = String.format(Locale.US, Constants.STATS_CONFIG_URL_TEMPLATE, RudderClient.getWriteKey());
        RudderLogger.logDebug("MetricsStatsManager: downloadConfigJson: configUrl: " + configUrl);
        this.parseConfigJson(rudderHttpClient.get(configUrl, null));
    }

    private void parseConfigJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            metricsConfig = new Gson().fromJson(json, MetricsConfig.class);
        }
    }

    static MetricsStatsManager getInstance(Application application) {
        if (instance == null) {
            instance = new MetricsStatsManager(application);
        }
        return instance;
    }

    boolean isEnabled() {
        return metricsConfig == null || metricsConfig.isEnabled();
    }

    private enum MetricsStats implements StatsInterface {
        EVENT_SIZE() {
            @Override
            @NonNull
            public String getQuerySql() {
                return String.format(Locale.US, "SELECT %s FROM %s", METRICS_EVENT_FIELD_SIZE, METRICS_EVENT_TABLE_NAME);
            }

            @NonNull
            @Override
            public String getParamPrefix() {
                return "eventSize";
            }
        },
        BATCH_SIZE {
            @NonNull
            @Override
            public String getQuerySql() {
                // only consider successful batch events
                return String.format(Locale.US, "SELECT %s FROM %s WHERE %s=1", METRICS_DATA_PLANE_FIELD_SIZE, METRICS_DATA_PLANE_TABLE_NAME, METRICS_DATA_PLANE_FIELD_SUCCESS);
            }

            @NonNull
            @Override
            public String getParamPrefix() {
                return "batchSize";
            }
        },
        DATA_PLANE_RESPONSE_TIME {
            @NonNull
            @Override
            public String getQuerySql() {
                return String.format(Locale.US, "SELECT %s FROM %s WHERE %s=1", METRICS_DATA_PLANE_FIELD_RESPONSE_TIME, METRICS_DATA_PLANE_TABLE_NAME, METRICS_DATA_PLANE_FIELD_SUCCESS);
            }

            @NonNull
            @Override
            public String getParamPrefix() {
                return "dataPlaneResponseTime";
            }
        },
        CONFIG_RESPONSE_TIME {
            @NonNull
            @Override
            public String getQuerySql() {
                return String.format(Locale.US, "SELECT %s FROM %s WHERE %s=1", METRICS_CONFIG_PLANE_FIELD_RESPONSE_TIME, METRICS_CONFIG_PLANE_TABLE_NAME, METRICS_CONFIG_PLANE_FIELD_SUCCESS);
            }

            @NonNull
            @Override
            public String getParamPrefix() {
                return "configPlaneResponseTime";
            }
        }
    }

    private interface StatsInterface {
        @NonNull
        String getQuerySql();

        @NonNull
        String getParamPrefix();
    }
}
