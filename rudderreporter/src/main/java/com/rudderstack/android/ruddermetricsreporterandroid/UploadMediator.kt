/*
 * Creator: Debanjan Chatterjee on 19/06/23, 7:57 pm Last modified: 19/06/23, 7:57 pm
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

package com.rudderstack.android.ruddermetricsreporterandroid

import com.rudderstack.android.ruddermetricsreporterandroid.error.ErrorModel
import com.rudderstack.android.ruddermetricsreporterandroid.metrics.MetricModel

fun interface UploadMediator {
    fun upload(
        metrics: List<MetricModel<out Number>>,
        error: ErrorModel,
        callback: (success: Boolean) -> Unit,
    )
}