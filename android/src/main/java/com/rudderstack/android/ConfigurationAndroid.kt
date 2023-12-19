/*
 * Creator: Debanjan Chatterjee on 28/11/23, 5:37 pm Last modified: 28/11/23, 10:00 am
 * Copyright: All rights reserved Ⓒ 2023 http://rudderstack.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.rudderstack.android

import android.app.Application
import com.rudderstack.android.internal.AndroidLogger
import com.rudderstack.android.storage.AndroidStorage
import com.rudderstack.android.storage.AndroidStorageImpl
import com.rudderstack.core.Base64Generator
import com.rudderstack.core.Configuration
import com.rudderstack.core.Logger
import com.rudderstack.core.RetryStrategy
import com.rudderstack.core.RudderOptions
import com.rudderstack.rudderjsonadapter.JsonAdapter
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ConfigurationAndroid internal constructor(configuration: Configuration,
                                                val application: Application,
                                                val anonymousId: String= UUID.randomUUID()
                                                    .toString(),
                                                val userId: String? = null,
                                                val trackLifecycleEvents: Boolean = Defaults.TRACK_LIFECYCLE_EVENTS,
                                                val recordScreenViews: Boolean = Defaults.RECORD_SCREEN_VIEWS,
                                                val isPeriodicFlushEnabled: Boolean = Defaults.IS_PERIODIC_FLUSH_ENABLED,
                                                val autoCollectAdvertId: Boolean = Defaults.AUTO_COLLECT_ADVERT_ID,
                                                val multiProcessEnabled: Boolean = Defaults.MULTI_PROCESS_ENABLED,
                                                val defaultProcessName: String?= Defaults.DEFAULT_PROCESS_NAME,
                                                val useContentProvider: Boolean = Defaults.USE_CONTENT_PROVIDER,
                                                val advertisingId: String? = null,
                                                val deviceToken: String? = null,
                                                override val storage: AndroidStorage = AndroidStorageImpl(),
                                                val advertisingIdFetchExecutor : ExecutorService? = null,
) : com.rudderstack.core.Configuration by configuration {
    /**
     * TODO write documentation
     *
     * @property application
     * @property anonymousId
     * @property userId
     * @property trackLifecycleEvents
     * @property recordScreenViews
     * @property isPeriodicFlushEnabled
     * @property autoCollectAdvertId
     * @property multiProcessEnabled
     * @property defaultProcessName
     * @property useContentProvider
     * @property advertisingId
     * @property deviceToken
     * @property storage
     * @property advertisingIdFetchExecutor
     * @constructor
     * TODO
     *
     * @param jsonAdapter
     * @param options
     * @param flushQueueSize
     * @param maxFlushInterval
     * @param isOptOut
     * @param shouldVerifySdk
     * @param sdkVerifyRetryStrategy
     * @param dataPlaneUrl
     * @param controlPlaneUrl
     * @param logger
     * @param analyticsExecutor
     * @param networkExecutor
     * @param base64Generator
     */
    constructor(
    application: Application,
    jsonAdapter: JsonAdapter,
    anonymousId: String= AndroidUtils.getDeviceId(
    application
    ),
    userId: String? = null,
    options: RudderOptions = RudderOptions.defaultOptions(),
    flushQueueSize: Int = Defaults.DEFAULT_FLUSH_QUEUE_SIZE,
    maxFlushInterval: Long = Defaults.DEFAULT_MAX_FLUSH_INTERVAL,
    isOptOut: Boolean = false,
    shouldVerifySdk: Boolean = Defaults.SHOULD_VERIFY_SDK,
    gzipEnabled: Boolean = Defaults.GZIP_ENABLED,
    sdkVerifyRetryStrategy: RetryStrategy = RetryStrategy.exponential(),
    dataPlaneUrl: String? = null, //defaults to https://hosted.rudderlabs.com
    controlPlaneUrl: String? = null, //defaults to https://api.rudderlabs.com/
    trackLifecycleEvents: Boolean = Defaults.TRACK_LIFECYCLE_EVENTS,
    recordScreenViews: Boolean = Defaults.RECORD_SCREEN_VIEWS,
    isPeriodicFlushEnabled: Boolean = Defaults.IS_PERIODIC_FLUSH_ENABLED,
    autoCollectAdvertId: Boolean = Defaults.AUTO_COLLECT_ADVERT_ID,
    multiProcessEnabled: Boolean = Defaults.MULTI_PROCESS_ENABLED,
    defaultProcessName: String?= Defaults.DEFAULT_PROCESS_NAME,
    useContentProvider: Boolean = Defaults.USE_CONTENT_PROVIDER,
    advertisingId: String? = null,
    deviceToken: String? = null,
    logger: Logger = AndroidLogger,
    storage: AndroidStorage = AndroidStorageImpl(),
    analyticsExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
    networkExecutor: ExecutorService = Executors.newCachedThreadPool(),
    advertisingIdFetchExecutor : ExecutorService? = null,
    base64Generator: Base64Generator = AndroidUtils.defaultBase64Generator(),
//    val defaultTraits: IdentifyTraits? = null, // will be added by default to each message
//    val defaultExternalIds: List<Map<String, String>>? = null, // will be added by default to each message
//    val defaultContextMap: Map<String, Any>? = null, // will be added by default to each message
//    val contextAddOns: Map<String, Any>? = null // will be added by default to each message
    ): this(
        Configuration(
            jsonAdapter,
            options,
            flushQueueSize,
            maxFlushInterval,
            isOptOut,
            shouldVerifySdk,
            gzipEnabled,
            sdkVerifyRetryStrategy,
            dataPlaneUrl,
            controlPlaneUrl,
            logger,
            storage,
            analyticsExecutor,
            networkExecutor,
            base64Generator
        ),
        application,
        anonymousId,
        userId,
        trackLifecycleEvents,
        recordScreenViews,
        isPeriodicFlushEnabled,
        autoCollectAdvertId,
        multiProcessEnabled,
        defaultProcessName,
        useContentProvider,
        advertisingId,
        deviceToken,
        storage,
        advertisingIdFetchExecutor
    )


    fun copy(
        jsonAdapter: JsonAdapter = this.jsonAdapter,
        options: RudderOptions = this.options,
        flushQueueSize: Int = this.flushQueueSize,
        maxFlushInterval: Long = this.maxFlushInterval,
        isOptOut: Boolean = this.isOptOut,
        gzipEnabled: Boolean = Defaults.GZIP_ENABLED,
        sdkVerifyRetryStrategy: RetryStrategy = this.sdkVerifyRetryStrategy,
        dataPlaneUrl: String = this.dataPlaneUrl,
        logger: Logger = this.logger,
        analyticsExecutor: ExecutorService = this.analyticsExecutor,
        networkExecutor: ExecutorService = this.networkExecutor,
        advertisingIdFetchExecutor : ExecutorService? = this.advertisingIdFetchExecutor,
        base64Generator: Base64Generator = this.base64Generator,
        anonymousId: String = this.anonymousId,
        userId: String? = this.userId,
        advertisingId: String? = this.advertisingId,
        deviceToken: String? = this.deviceToken
    ) = ConfigurationAndroid(
        application,
        jsonAdapter,
        anonymousId,
        userId,
        options,
        flushQueueSize,
        maxFlushInterval,
        isOptOut,
        gzipEnabled,
        shouldVerifySdk,
        sdkVerifyRetryStrategy,
        dataPlaneUrl,
        controlPlaneUrl,
        trackLifecycleEvents,
        recordScreenViews,
        isPeriodicFlushEnabled,
        autoCollectAdvertId,
        multiProcessEnabled,
        defaultProcessName,
        useContentProvider,
        advertisingId,
        deviceToken,
        logger,
        storage,
        analyticsExecutor,
        networkExecutor,
        advertisingIdFetchExecutor,
        base64Generator,
//        defaultTraits,
//        defaultExternalIds,
//        defaultContextMap,
//        contextAddOns
    )

    internal object Defaults{
        val GZIP_ENABLED: Boolean = true
        const val SHOULD_VERIFY_SDK: Boolean = true
        const val TRACK_LIFECYCLE_EVENTS = true
        const val RECORD_SCREEN_VIEWS = true
        const val IS_PERIODIC_FLUSH_ENABLED = false
        const val AUTO_COLLECT_ADVERT_ID = true
        const val MULTI_PROCESS_ENABLED = false
        val DEFAULT_PROCESS_NAME: String? = null
        const val USE_CONTENT_PROVIDER = false
        const val DEFAULT_FLUSH_QUEUE_SIZE = 30
        const val DEFAULT_MAX_FLUSH_INTERVAL = 10 * 1000L
    }
}