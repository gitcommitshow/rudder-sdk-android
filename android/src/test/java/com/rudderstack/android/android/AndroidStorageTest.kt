/*
 * Creator: Debanjan Chatterjee on 20/07/22, 11:57 PM Last modified: 20/07/22, 11:57 PM
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

package com.rudderstack.android.android

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rudderstack.android.ConfigurationAndroid
import com.rudderstack.android.android.utils.TestExecutor
import com.rudderstack.android.android.utils.busyWait
import com.rudderstack.android.storage.AndroidStorage
import com.rudderstack.android.storage.AndroidStorageImpl
import com.rudderstack.core.RudderUtils
import com.rudderstack.core.Storage
import com.rudderstack.core.internal.states.ConfigurationsState
import com.rudderstack.gsonrudderadapter.GsonAdapter
import com.rudderstack.jacksonrudderadapter.JacksonAdapter
import com.rudderstack.models.TrackMessage
import com.rudderstack.moshirudderadapter.MoshiAdapter
import com.rudderstack.rudderjsonadapter.JsonAdapter
import io.mockk.every
import junit.framework.TestSuite
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config
import java.util.*

/**
 * Test class for testing the AndroidStorageImpl class
 */

@RunWith(MockitoJUnitRunner::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.P])
abstract class AndroidStorageTest {
    lateinit var mockConfig: ConfigurationAndroid
    protected abstract val jsonAdapter: JsonAdapter
    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        val storage = AndroidStorageImpl(ApplicationProvider.getApplicationContext(), false
        )
        mockConfig = mock()
        whenever(mockConfig.storage).thenReturn(storage)
        whenever(mockConfig.jsonAdapter).thenReturn(jsonAdapter)
        whenever(mockConfig.analyticsExecutor).thenReturn(TestExecutor())
        whenever(mockConfig.application).thenReturn(ApplicationProvider.getApplicationContext())
        ConfigurationsState.update(mockConfig)
    }
    @After
    fun destroy(){
        val storage = ConfigurationsState.value?.storage as AndroidStorageImpl
        storage.clearStorage()
        storage.shutdown()
    }
    @Test
    fun `test drop back pressure strategies`() {
        val storage = ConfigurationsState.value?.storage as AndroidStorageImpl

        storage.clearStorage()
        //we check the storage directly
        val events = (1..20).map {
            TrackMessage.create("event:$it", RudderUtils.timeStamp)
        }
        storage.setStorageCapacity(10)
        storage.setBackpressureStrategy(Storage.BackPressureStrategy.Drop) // first 10 will be there
        storage.saveMessage(*events.toTypedArray())
        while (storage.getDataSync().size != 10) {
        }
        //let's busy wait to check if more data is being saved
        busyWait(500L)
        val first10Events = events.take(10).map { it.eventName }
        val last10Events = events.takeLast(10).map { it.eventName }
        val saved = storage.getDataSync()
        MatcherAssert.assertThat(
            saved, Matchers.allOf(
                Matchers.iterableWithSize(10), Matchers.everyItem(
                    Matchers.allOf(
                        Matchers.isA(TrackMessage::class.java), Matchers.hasProperty(
                            "eventName", Matchers.allOf(
                                Matchers.`in`(first10Events),
                                Matchers.not(Matchers.`in`(last10Events))
                            )
                        )
                    )
                )
//            contains(last10Events)
            )
        )
    }

    @Test
    fun `test latest back pressure strategies`() {
        val storage = ConfigurationsState.value?.storage as AndroidStorageImpl

        storage.clearStorage()
        val events = (1..20).map {
            TrackMessage.create("event:$it", RudderUtils.timeStamp)
        }
        storage.setStorageCapacity(10)
        storage.setBackpressureStrategy(Storage.BackPressureStrategy.Latest) // last 10 will be there

        storage.saveMessage(*events.toTypedArray())
        while (storage.getDataSync().size != 10) {
        }
        busyWait(500L)
        val first10Events = events.take(10).map { it.eventName }
        val last10Events = events.takeLast(10).map { it.eventName }
        MatcherAssert.assertThat(
            storage.getDataSync(), Matchers.allOf(
                Matchers.iterableWithSize(10),
                Matchers.everyItem(
                    Matchers.allOf(
                        Matchers.isA(TrackMessage::class.java),
                        Matchers.hasProperty(
                            "eventName", Matchers.allOf(
                                Matchers.`in`(last10Events),
                                Matchers.not(Matchers.`in`(first10Events))
                            )
                        )
                    )
                )
//            contains(last10Events)
            )
        )
    }

}
@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class GsonStorageTest : AndroidStorageTest(){
    override val jsonAdapter: JsonAdapter
        get() = GsonAdapter()
}
@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class JacksonStorageTest : AndroidStorageTest(){
    override val jsonAdapter: JsonAdapter
        get() = JacksonAdapter()
}
@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class MoshiStorageTest : AndroidStorageTest(){
    override val jsonAdapter: JsonAdapter
        get() = MoshiAdapter()
}
@RunWith(Suite::class)
@Suite.SuiteClasses(
    GsonStorageTest::class,
    JacksonStorageTest::class,
    MoshiStorageTest::class
)
class AndroidStorageTestSuite : TestSuite(){}