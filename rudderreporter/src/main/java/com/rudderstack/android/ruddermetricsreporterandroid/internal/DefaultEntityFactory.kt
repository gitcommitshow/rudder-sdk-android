/*
 * Creator: Debanjan Chatterjee on 15/06/23, 5:01 pm Last modified: 15/06/23, 5:01 pm
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

package com.rudderstack.android.ruddermetricsreporterandroid.internal

import com.rudderstack.android.repository.Entity
import com.rudderstack.android.repository.EntityFactory
import com.rudderstack.android.ruddermetricsreporterandroid.models.ErrorEntity
import com.rudderstack.android.ruddermetricsreporterandroid.models.LabelEntity
import com.rudderstack.android.ruddermetricsreporterandroid.models.MetricEntity
import com.rudderstack.android.ruddermetricsreporterandroid.models.SnapshotEntity

class DefaultEntityFactory : EntityFactory {

    override fun <T : Entity> getEntity(entity: Class<T>, values: Map<String, Any?>): T? {
        return when (entity) {
            MetricEntity::class.java -> MetricEntity.create(values) as? T?
            LabelEntity::class.java -> LabelEntity.create(values) as? T?
            ErrorEntity::class.java -> ErrorEntity.create(values) as? T?
            SnapshotEntity::class.java -> SnapshotEntity.create(values) as? T?
            else -> null
        }
    }
}