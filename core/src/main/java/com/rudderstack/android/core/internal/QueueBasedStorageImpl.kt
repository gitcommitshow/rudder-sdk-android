/*
 * Creator: Debanjan Chatterjee on 30/12/21, 6:25 PM Last modified: 30/12/21, 6:25 PM
 * Copyright: All rights reserved Ⓒ 2021 http://rudderstack.com
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

import com.rudderstack.android.core.Settings
import com.rudderstack.android.core.State
import com.rudderstack.android.core.Storage
import com.rudderstack.android.core.internal.states.SettingsState
import com.rudderstack.android.models.Message
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

internal class QueueBasedStorageImpl(
    private val queue: Queue<Message> = LinkedList()
) : Storage {
    private var cachedContext: Map<String,String> = mapOf()
    private var dataChangeListeners = listOf<(List<Message>) -> Unit>()
    private var _isOptOut = false
    private var _optOutTime = -1L
    private var _optInTime = -1L

    override fun saveMessage(vararg messages: Message) {
        queue.addAll(messages)
        onDataChange()
    }

    override fun deleteMessages(messages: List<Message>) {
        queue.removeAll(messages)
        onDataChange()
    }

    override fun addDataChangeListener(listener: (data: List<Message>) -> Unit) {
        dataChangeListeners = dataChangeListeners + listener
    }

    override fun getData(callback: (List<Message>) -> Unit) {
        callback.invoke(queue.toList())
    }

    override fun cacheContext(context: Map<String, String>) {
        cachedContext = context
    }

    override val context: Map<String, String>
        get() = cachedContext

    override fun saveOptOut(optOut: Boolean) {
         _isOptOut = optOut
        if(optOut){
            _optOutTime = System.currentTimeMillis()
        }else
            _optInTime = System.currentTimeMillis()
    }

    override val isOptedOut: Boolean
        get() = _isOptOut
    override val optOutTime: Long
        get() = _optOutTime
    override val optInTime: Long
        get() = _optInTime

    private fun onDataChange(){
        val msgs = queue.toList()
        dataChangeListeners.forEach {
            it.invoke(msgs)
        }
    }
}