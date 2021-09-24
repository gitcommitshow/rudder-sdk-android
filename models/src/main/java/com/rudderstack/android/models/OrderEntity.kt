/*
 * Creator: Debanjan Chatterjee on 24/09/21, 11:09 PM Last modified: 17/09/21, 12:00 AM
 * Copyright: All rights reserved Ⓒ 2021 http://hiteshsahu.com
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

package com.rudderstack.android.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class OrderEntity(
    @SerializedName("order_id")
    @get:JsonProperty("order_id")
    val orderId: Int,
    @SerializedName("quantity")
    @get:JsonProperty("quantity")
    var quantity : Int,
    @SerializedName("total_price")
    @get:JsonProperty("total_price")
    val totalPrice: Double)
