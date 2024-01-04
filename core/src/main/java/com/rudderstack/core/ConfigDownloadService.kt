/*
 * Creator: Debanjan Chatterjee on 24/01/22, 8:13 PM Last modified: 24/01/22, 8:13 PM
 * Copyright: All rights reserved Ⓒ 2022 http://rudderstack.com
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

package com.rudderstack.core

import com.rudderstack.models.RudderServerConfig

/**
 * Download config for SDK.
 * Config aids in usage of device mode plugins.
 *
 */
interface ConfigDownloadService {
    /**
     * Fetches the config from
     *
     * @param platform
     * @param libraryVersion
     * @param osVersion
     */
    fun download(platform : String, libraryVersion : String, osVersion : String, retryStrategy: RetryStrategy,
                 callback: (success : Boolean, RudderServerConfig?, lastErrorMsg : String?) -> Unit)

    /**
     * Release resources if any
     *
     */
    fun shutDown()
}