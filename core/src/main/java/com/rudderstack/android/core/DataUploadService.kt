/*
 * Creator: Debanjan Chatterjee on 06/01/22, 11:07 AM Last modified: 06/01/22, 11:07 AM
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

package com.rudderstack.android.core

import com.rudderstack.android.models.Message

/**
 * Class to handle data upload to server
 *
 */
interface DataUploadService {
    /**
     * Uploads data to cloud
     *
     * @param data The list of messages to upload
     * @param extraInfo If any data needs to be added to the body
     * @param callback Callback providing either success or failure status of upload
     */
    fun upload(data: List<Message>, extraInfo : Map<String,String>? = null, callback: (success: Boolean) -> Unit)

    /**
     * Uploads data synchronously
     *
     * @param data The list of messages to upload
     * @param extraInfo If any data needs to be added to the body
     * @return status of upload, true if success, false otherwise
     */
    fun uploadSync(data: List<Message>, extraInfo : Map<String,String>? = null) : Boolean

    /**
     * Service no longer needed, release resources
     *
     */
    fun shutdown()

    /**
     * true if [shutdown] has been called on this object else false
     */
    val isShutdown: Boolean
}