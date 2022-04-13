/*
 * Creator: Debanjan Chatterjee on 11/04/22, 3:59 PM Last modified: 11/04/22, 3:53 PM
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

import com.rudderstack.android.models.IdentifyTraits
import com.rudderstack.android.models.Message
import com.rudderstack.android.models.RudderServerConfig
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@Suppress("ThrowableNotThrown")
class BasicStorageImpl(
    /**
     * queue size should be greater than or equals [Storage.MAX_STORAGE_CAPACITY]
     */
    private val queue: Queue<Message> = LinkedBlockingQueue(),
    private val logger: Logger
) : Storage {
    private var backPressureStrategy = Storage.BackPressureStrategy.Drop

    private var _storageCapacity = Storage.MAX_STORAGE_CAPACITY
    private var _maxFetchLimit = Storage.MAX_FETCH_LIMIT

    private var _cachedContext: Map<String, Any> = mapOf()
    private var _dataChangeListeners = listOf<Storage.DataListener>()
    private var _isOptOut = false
    private var _optOutTime = -1L
    private var _optInTime = -1L

    private var _serverConfig: RudderServerConfig? = null
    private var _traits: IdentifyTraits? = null
    private var _externalIds: List<Map<String, String>>? = null
    private var _anonymousId: String? = null

    /**
     * This queue holds the messages that are generated prior to destinations waking up
     */
    private val startupQ = LinkedList<Message>()

    private val serverConfigFile = File("/temp/rudder-analytics/server_config")
    override fun setStorageCapacity(storageCapacity: Int) {
        _storageCapacity = storageCapacity
    }

    override fun setMaxFetchLimit(limit: Int) {
        _maxFetchLimit = limit
    }

    override fun saveMessage(vararg messages: Message) {
        val excessMessages = queue.size + messages.size - Storage.MAX_STORAGE_CAPACITY
        if (excessMessages > 0) {

            //a block to call data listener
            val dataFailBlock: List<Message>.() -> Unit = {
                _dataChangeListeners.forEach {
                    it.onDataDropped(this, IllegalArgumentException("Storage Capacity Exceeded"))
                }
            }

            if (backPressureStrategy == Storage.BackPressureStrategy.Drop) {

                logger.warn(log = "Max storage capacity reached, dropping last $excessMessages latest events")

                (messages.size - excessMessages).takeIf {
                    it > 0
                }?.apply {
                    synchronized(this) {
                        queue.addAll(messages.take(this))
                    }

                    //callback
                    messages.takeLast(excessMessages).run(dataFailBlock)

                } ?: messages.toList().run(dataFailBlock)

            } else {
                logger.warn(log = "Max storage capacity reached, dropping first $excessMessages oldest events")
                val tobeRemovedList = ArrayList<Message>(excessMessages)
                var counter = excessMessages
                synchronized(this) {

                    while (counter > 0) {
                        val item = queue.poll()
                        if (item != null) {
                            counter--
                            tobeRemovedList.add(item)
                        } else
                            break
                    }
                }
                //callback
                tobeRemovedList.run(dataFailBlock)
            }
        } else {
            synchronized(this) {

                queue.addAll(messages)
            }
        }


        onDataChange()
    }

    override fun setBackpressureStrategy(strategy: Storage.BackPressureStrategy) {
        backPressureStrategy = strategy
    }

    override fun deleteMessages(messages: List<Message>) {
        synchronized(this) {

            queue.removeAll(messages)
        }
        onDataChange()
    }

    override fun addDataListener(listener: Storage.DataListener) {
        _dataChangeListeners = _dataChangeListeners + listener
    }

    override fun removeDataListener(listener: Storage.DataListener) {
        _dataChangeListeners = _dataChangeListeners - listener
    }

    override fun getData(offset: Int, callback: (List<Message>) -> Unit) {
        callback.invoke(
            synchronized(this) {
                if (queue.size <= offset) emptyList() else
                    queue.toMutableList().takeLast(queue.size - offset)
                        .take(Storage.MAX_FETCH_LIMIT).toList()
            })
    }

    override fun getDataSync(offset: Int): List<Message> {
        return if (queue.size <= offset) emptyList() else queue.toList()
            .takeLast(queue.size - offset).take(_maxFetchLimit)
    }


    override fun cacheContext(context: Map<String, Any>) {
        _cachedContext = context
    }

    override val context: Map<String, Any>
        get() = _cachedContext

    override fun saveServerConfig(serverConfig: RudderServerConfig) {
        try {
            if (!serverConfigFile.exists()) {
                serverConfigFile.parentFile.mkdirs()
                serverConfigFile.createNewFile()
            }
            val fos = FileOutputStream(serverConfigFile)
            val oos = ObjectOutputStream(fos)

            oos.writeObject(serverConfig)
            oos.flush()
            fos.close()

        } catch (ex: Exception) {
            logger.error(log = "Server Config cannot be saved", throwable = ex)
        }
    }

    override fun saveOptOut(optOut: Boolean) {
        _isOptOut = optOut
        if (optOut) {
            _optOutTime = System.currentTimeMillis()
        } else
            _optInTime = System.currentTimeMillis()
    }

    override fun saveTraits(traits: IdentifyTraits) {
        this._traits = traits
    }

    override fun saveExternalIds(externalIds: List<Map<String, String>>) {
        _externalIds = externalIds
    }

    override fun clearExternalIds() {
        _externalIds = listOf()
    }

    override fun saveAnonymousId(anonymousId: String) {
        _anonymousId = anonymousId
    }

    override fun saveStartupMessageInQueue(message: Message) {
        startupQ.add(message)
    }

    override fun clearStartupQueue() {
        startupQ.clear()
    }

    override fun shutdown() {
        synchronized(this) {
            queue.clear()
        }
    }


    override val serverConfig: RudderServerConfig?
        get() = _serverConfig

    override val startupQueue: List<Message>
        get() = startupQ

    override val isOptedOut: Boolean
        get() = _isOptOut
    override val optOutTime: Long
        get() = _optOutTime
    override val optInTime: Long
        get() = _optInTime
    override val traits: IdentifyTraits?
        get() = _traits
    override val externalIds: List<Map<String, String>>?
        get() = _externalIds
    override val anonymousId: String?
        get() = _anonymousId

    private fun onDataChange() {
        val msgs = synchronized(this) {
            queue.take(Storage.MAX_FETCH_LIMIT).toList()
        }
        _dataChangeListeners.forEach {
            it.onDataChange(msgs)
        }
    }
}