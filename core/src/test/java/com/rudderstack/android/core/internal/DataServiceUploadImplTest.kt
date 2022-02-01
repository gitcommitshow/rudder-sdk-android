/*
 * Creator: Debanjan Chatterjee on 12/01/22, 11:51 AM Last modified: 12/01/22, 11:49 AM
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

package com.rudderstack.android.core.internal

import com.rudderstack.android.core.DataUploadService
import com.rudderstack.android.core.Settings
import com.rudderstack.android.core.internal.states.SettingsState
import com.rudderstack.android.gsonrudderadapter.GsonAdapter
import com.rudderstack.android.jacksonrudderadapter.JacksonAdapter
import com.rudderstack.android.models.*
import com.rudderstack.android.moshirudderadapter.MoshiAdapter
import com.rudderstack.android.rudderjsonadapter.JsonAdapter
import com.rudderstack.android.web.WebService
import junit.framework.TestSuite
import org.awaitility.Awaitility
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

abstract class DataServiceUploadImplTest {
    protected abstract val jsonAdapter: JsonAdapter
    private lateinit var dataServiceImpl: DataUploadService
    private val testMessagesList = listOf<Message>(
        TrackMessage("m-1", anonymousId = "anon-1", timestamp = "09-01-2022"),
        TrackMessage("m-2", anonymousId = "anon-2", timestamp = "09-01-2022"),
        TrackMessage("m-3", anonymousId = "anon-3", timestamp = "09-01-2022"),
        TrackMessage("m-4", anonymousId = "anon-4", timestamp = "09-01-2022"),
        TrackMessage("m-5", anonymousId = "anon-5", timestamp = "09-01-2022"),
        TrackMessage("m-6", anonymousId = "anon-6", timestamp = "09-01-2022"),
        TrackMessage("m-7", anonymousId = "anon-7", timestamp = "09-01-2022"),
        TrackMessage("m-8", anonymousId = "anon-8", timestamp = "09-01-2022"),
        TrackMessage("m-9", anonymousId = "anon-9", timestamp = "09-01-2022"),
        TrackMessage("m-10", anonymousId = "anon-10", timestamp = "09-01-2022"),
        TrackMessage("m-11", anonymousId = "anon-11", timestamp = "09-01-2022"),
    )

    @Before
    fun setup() {
        dataServiceImpl = DataUploadServiceImpl(
            "1xXCubSHWXbpBI2h6EpCjKOsxmQ",
            jsonAdapter, dataPlaneUrl = "https://rudderstacgwyx.dataplane.rudderstack.com"
        )

    }

    @Test
    fun testUpload() {
        val isComplete = AtomicBoolean(false)
        dataServiceImpl.upload(testMessagesList) {
            assertThat(it, `is`(true))
            isComplete.set(true)
        }
        Awaitility.await().atMost(1, TimeUnit.MINUTES).untilTrue(isComplete)
    }
}

class DataServiceUploadTestWithJackson : DataServiceUploadImplTest() {
    override val jsonAdapter: JsonAdapter = JacksonAdapter()

}
class DataServiceUploadTestWithGson : DataServiceUploadImplTest() {
    override val jsonAdapter: JsonAdapter = GsonAdapter()

}
class DataServiceUploadTestWithMoshi : DataServiceUploadImplTest() {
    override val jsonAdapter: JsonAdapter = MoshiAdapter()

}

@RunWith(Suite::class)
@Suite.SuiteClasses(
    DataServiceUploadTestWithJackson::class, DataServiceUploadTestWithGson::class,
    DataServiceUploadTestWithMoshi::class
)
class DataUploadTestSuite : TestSuite() {

}