/*
 * Creator: Debanjan Chatterjee on 28/12/21, 4:32 PM Last modified: 28/12/21, 4:25 PM
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

package com.rudderstack.core

/**
 * Can be set as global or customised options for each message.
 * If no customised option is set for a message, global options will be used.
 * Users can pass any object as values, but in case of complex classes, please check with the
 * serializer/deserializer adapter being used
 * @property externalIds External ids can be used for assigning extra ids for different destinations
 * on the transformer side.
 * @property integrations The integrations to which these messages will be delivered. If empty, the
 * message will be delivered to all destinations added to Analytics.
 * @property customContexts Custom context elements that are going to be sent with message
 */
class RudderOptions private constructor(
    val externalIds: List<Map<String, String>>,
    val integrations: Map<String, Boolean>,
    val customContexts: Map<String, Any>,

    ) {

    companion object {

        /**
         * Default options
         *
         */
        @JvmStatic
        fun defaultOptions(): RudderOptions = RudderOptions(listOf(), mapOf(), mapOf())
    }

    /**
     * Creates a new builder from this object.
     *
     */
    fun newBuilder(): Builder {
        return Builder().apply {
            externalIds.map {
                it["type"]?.let { type ->
                    it["id"]?.let { id ->
                        withExternalId(type, id)
                    }
                }
            }
            withIntegrations(integrations)
            withCustomContexts(customContexts)
        }
    }

    /**
     * Builder for RudderOption
     *
     */
    class Builder {

        private var _externalIds: MutableList<MutableMap<String, String>> = mutableListOf()
        private var _integrations: Map<String, Boolean> = mapOf()
        private var _customContexts: Map<String, Any> = mapOf()

        fun withExternalId(type: String, id: String): Builder {
            val existingId = _externalIds.find { it["type"] == type }
            existingId?.let { it["id"] = id } ?: run {
                _externalIds.add(mutableMapOf("type" to type, "id" to id))
            }
            return this
        }

        fun withIntegrations(integrations: Map<String, Boolean>): Builder {
            this._integrations = integrations
            return this
        }

        fun withCustomContexts(customContexts: Map<String, Any>): Builder {
            this._customContexts = customContexts
            return this
        }

        fun build() = RudderOptions(_externalIds, _integrations, _customContexts)
    }

    override fun equals(other: Any?): Boolean {
        return other is RudderOptions &&
                other.externalIds == this.externalIds &&
                other.integrations == this.integrations &&
                other.customContexts == this.customContexts
    }

    override fun hashCode(): Int {
        return externalIds.hashCode() + integrations.hashCode() + customContexts.hashCode()
    }

}
