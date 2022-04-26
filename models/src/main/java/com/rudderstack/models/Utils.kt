/*
 * Creator: Debanjan Chatterjee on 23/03/22, 4:48 PM Last modified: 23/03/22, 4:48 PM
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
@file:JvmName("Utils")

package com.rudderstack.models

/**
 * An utility function to create a context Map
 *
 * @param traits
 * @param externalIds
 * @param customContextMap
 * @return
 */
fun createContext(
    traits: Map<String, Any?>? = null,
    externalIds: List<Map<String, String>>? = null,
    customContextMap: Map<String, Any?>? = null
) : MessageContext =
    mapOf(
        Constants.TRAITS_ID to traits,
        Constants.EXTERNAL_ID to externalIds,
        Constants.CUSTOM_CONTEXT_MAP_ID to customContextMap
    )