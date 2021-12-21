/*
 * Creator: Debanjan Chatterjee on 06/12/21, 8:01 PM Last modified: 06/12/21, 8:01 PM
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

package com.rudderstack.android.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.*
import java.util.*
import kotlin.collections.HashMap

typealias MessageContext = Map<String, String>
typealias PageProperties = Map<String, String>
typealias ScreenProperties = Map<String, String>
typealias TrackProperties = Map<String, String>
typealias IdentifyProperties = Map<String, String>
//integrations might change from String,Boolean to String, Object at a later point of time
typealias MessageIntegrations = MutableMap<String, Any>
typealias MessageDestinationProps = MutableMap<String, Map<*, *>>
typealias GroupTraits = Map<String, String>

sealed class Message(

    /**
     * @return Type of event
     * @see EventType
     */
    @SerializedName("type")
    @JsonProperty("type")
    @Json(name = "type")
    internal var type: EventType,

    @SerializedName("messageId")
    @JsonProperty("messageId")
    @Json(name = "messageId")
    val messageId: String = String.format(
        Locale.US,
        "%d-%s",
        System.currentTimeMillis(),
        UUID.randomUUID().toString()
    ),

    @SerializedName("context")
    @JsonProperty("context")
    @Json(name = "context")
    //convert to json to put any object as value
    protected var context: MessageContext? = null,

    @SerializedName("anonymousId")
    @JsonProperty("anonymousId")
    @Json(name = "anonymousId")
    val anonymousId: String,

    /**
     * @return User ID for the event
     */
    @SerializedName("userId")
    @JsonProperty("userId")
    @Json(name = "userId")
    var userId: String? = null,

    @SerializedName("timestamp")
    @JsonProperty("timestamp")
    @Json(name = "timestamp")
    val timestamp: String,


    @SerializedName("channel")
    @JsonProperty("channel")
    @Json(name = "channel")
//    @ChannelMitigationMoshi
    private val _channel: String? = "",

    @SerializedName("destinationProps")
    @JsonProperty("destinationProps")
    @Json(name = "destinationProps")
    private var destinationProps: MessageDestinationProps? = null,

    @SerializedName("integrations")
    @JsonProperty("integrations")
    @Json(name = "integrations")
    val integrations: MessageIntegrations = HashMap(),








//    @Transient
//    private var customContexts: Map<String, Any>? = null
) {

    //ugly hack for moshi
    //https://github.com/square/moshi/issues/609#issuecomment-798805367
    @Transient
    val messageChanel : String = "server"
    get() {
        field
        return if(_channel.isNullOrEmpty()) "server" else _channel
    }

    fun getType() = type

    /*@Retention(AnnotationRetention.RUNTIME)
    @JsonQualifier
    annotation class ChannelMitigationMoshi

    class ChannelMoshiAdapter {
        @FromJson @ChannelMitigationMoshi fun fromJson(channel: String?):  String {
            return channel?:"server"
        }

        @ToJson fun toJson(@ChannelMitigationMoshi channel: String) : String? {
            return channel
        }
    }*/
    /*@Transient
    var rudderOption: RudderOption? = null
        set(rudderOption) {
            field = rudderOption
            if (rudderOption != null) {
                setIntegrations(rudderOption.getIntegrations())
                setCustomContexts(rudderOption.getCustomContexts())
            }
        }
*/
    /*fun setPreviousId(previousId: String?) {
        this.previousId = previousId
    }*/

    /*      fun setGroupTraits(groupTraits: RudderTraits?) {
              traits = groupTraits
          }

          fun setProperty(property: RudderProperty?) {
              if (property != null) properties = property.getMap()
          }

          fun setUserProperty(userProperty: RudderUserProperty) {
              userProperties = userProperty.getMap()
          }

          fun updateTraits(traits: RudderTraits?) {
              RudderElementCache.updateTraits(traits)
              updateContext()
          }

          fun updateTraits(traits: Map<String?, Any?>?) {
              RudderElementCache.updateTraits(traits)
              updateContext()
          }

          fun updateExternalIds(option: RudderOption?) {
              if (option != null) {
                  val externalIds: List<Map<String, Any>> = option.getExternalIds()
                  if (externalIds != null && !externalIds.isEmpty()) {
                      RudderElementCache.updateExternalIds(externalIds)
                      updateContext()
                  }
              }
          }
  */
    /*fun addIntegrationProps(integrationKey: String, isEnabled: Boolean, props: Map<*, *>) {
        integrations[integrationKey] = isEnabled
        if (isEnabled) {
            if (destinationProps == null) destinationProps = HashMap()
            destinationProps!![integrationKey] = props
        }
    }

    fun setIntegrations(integrations: Map<String, Any>?) {
        if (integrations == null) return
        for (key in integrations.keys) {
            this.integrations[key] = integrations[key] as Any
        }
    }*/

/*
        fun setCustomContexts(customContexts: Map<String, Any>?) {
            if (customContexts == null) return
            this.customContexts = customContexts
            context.setCustomContexts(customContexts)
        }

        fun getTraits(): Map<String, Any> {
            return context.getTraits()
        }
*/

    /**
     * @return Returns message level context
     *//*
        fun getContext(): RudderContext {
            return context
        }

        fun updateContext() {
            context = RudderElementCache.getCachedContext()
            if (customContexts != null) context.setCustomContexts(customContexts)
        }

        */
    /**
     * @return Integrations Map passed for the event
     *//*
        fun getIntegrations(): Map<String, Any?> {
            return integrations
        }

        init {
            context = RudderElementCache.getCachedContext()
            anonymousId = RudderContext.getAnonymousId()
            val traits: Map<String, Any> = context.getTraits()
            if (traits != null && traits.containsKey("id")) {
                userId = traits["id"].toString()
            }
        }*/

    enum class EventType(val value: String) {
        @SerializedName("Alias")
        @JsonProperty("Alias")
        @Json(name = "Alias")
        ALIAS("Alias"),

        @SerializedName("Group")
        @JsonProperty("Group")
        @Json(name = "Group")
        GROUP("Group"),

        @SerializedName("Page")
        @JsonProperty("Page")
        @Json(name = "Page")
        PAGE("Page"),

        @SerializedName("Screen")
        @JsonProperty("Screen")
        @Json(name = "Screen")
        SCREEN("Screen"),

        @SerializedName("Track")
        @JsonProperty("Track")
        @Json(name = "Track")
        TRACK("Track"),

        @SerializedName("Identify")
        @JsonProperty("Identify")
        @Json(name = "Identify")
        IDENTIFY("Identify")
    }
}

class AliasMessage(
    @JsonProperty("messageId")
    @Json(name = "messageId")
    messageId: String,
    @JsonProperty("context")
    @Json(name = "context")
    context: MessageContext? = null,
    @JsonProperty("anonymousId")
    @Json(name = "anonymousId")
    anonymousId: String,
    @JsonProperty("userId")
    @Json(name = "userId")
    userId: String? = null,
    @JsonProperty("timestamp")
    @Json(name = "timestamp")
    timestamp: String,
    @JsonProperty("channel")
    @Json(name = "channel")
    channel: String? = "",
    @JsonProperty("destinationProps")
    @Json(name = "destinationProps")
    destinationProps: MessageDestinationProps? = null,
    @JsonProperty("integrations")
    @Json(name = "integrations")
    integrations: MessageIntegrations = HashMap(),
    @SerializedName("previousId")
    @JsonProperty("previousId")
    @Json(name = "previousId")
    private var previousId: String? = null,

    ) : Message(
    EventType.ALIAS,
    messageId,
    context,
    anonymousId,
    userId,
    timestamp,
    channel,
    destinationProps,
    integrations
){

    }

@JsonIgnoreProperties(ignoreUnknown = true)
class GroupMessage(
    @JsonProperty("messageId")
    @Json(name = "messageId")
    messageId: String,
    @JsonProperty("context")
    @Json(name = "context")
    context: MessageContext? = null,
    @JsonProperty("anonymousId")
    @Json(name = "anonymousId")
    anonymousId: String,
    @JsonProperty("userId")
    @Json(name = "userId")
    userId: String? = null,
    @JsonProperty("timestamp")
    @Json(name = "timestamp")
    timestamp: String,
    @JsonProperty("channel")
    @Json(name = "channel")
    channel: String? = "",
    @JsonProperty("destinationProps")
    @Json(name = "destinationProps")
    destinationProps: MessageDestinationProps? = null,
    @JsonProperty("integrations")
    @Json(name = "integrations")
    integrations: MessageIntegrations = HashMap(),
    /**
     * @return Group ID for the event
     */
    @SerializedName("groupId")
    @JsonProperty("groupId")
    @Json(name = "groupId")
    var groupId: String? = null,

    @SerializedName("traits")
    @JsonProperty("traits")
    @Json(name = "traits")
    val traits: GroupTraits? = null,


    ) : Message(
    EventType.GROUP,
    messageId,
    context,
    anonymousId,
    userId,
    timestamp,
    channel,
    destinationProps,
    integrations
)


class PageMessage(
    @JsonProperty("messageId")
    @Json(name = "messageId")
    messageId: String,
    @JsonProperty("context")
    @Json(name = "context")
    context: MessageContext? = null,
    @JsonProperty("anonymousId")
    @Json(name = "anonymousId")
    anonymousId: String,
    @JsonProperty("userId")
    @Json(name = "userId")
    userId: String? = null,
    @JsonProperty("timestamp")
    @Json(name = "timestamp")
    timestamp: String,
    @JsonProperty("channel")
    @Json(name = "channel")
    channel: String? = "",
    @JsonProperty("destinationProps")
    @Json(name = "destinationProps")
    destinationProps: MessageDestinationProps? = null,
    @JsonProperty("integrations")
    @Json(name = "integrations")
    integrations: MessageIntegrations = HashMap(),
    /**
     * @return Name of the event tracked
     */

    @SerializedName("event")
    @JsonProperty("event")
    @Json(name = "event")
    var name: String? = null,

    /**
     * Get the properties back as set to the event
     * Always convert objects to it's json equivalent before setting it as values
     * @return Map of String-Object
     */

    @SerializedName("properties")
    @JsonProperty("properties")
    @Json(name = "properties")
    val properties: PageProperties? = null,

    @SerializedName("category")
    @JsonProperty("category")
    @Json(name = "category")
    val category: String? = null,

    ) : Message(
    EventType.PAGE,
    messageId,
    context,
    anonymousId,
    userId,
    timestamp,
    channel,
    destinationProps,
    integrations
)


class ScreenMessage(
    @JsonProperty("messageId")
    @Json(name = "messageId")
    messageId: String,
    @JsonProperty("context")
    @Json(name = "context")
    context: MessageContext? = null,
    @JsonProperty("anonymousId")
    @Json(name = "anonymousId")
    anonymousId: String,
    @JsonProperty("userId")
    @Json(name = "userId")
    userId: String? = null,
    @JsonProperty("timestamp")
    @Json(name = "timestamp")
    timestamp: String,
    @JsonProperty("channel")
    @Json(name = "channel")
    channel: String? = "",
    @JsonProperty("destinationProps")
    @Json(name = "destinationProps")
    destinationProps: MessageDestinationProps? = null,
    @JsonProperty("integrations")
    @Json(name = "integrations")
    integrations: MessageIntegrations = HashMap(),
    /**
     * @return Name of the event tracked
     */

    @SerializedName("event")
    @JsonProperty("event")
    @Json(name = "event")
    var name: String? = null,

    /**
     * Get the properties back as set to the event
     * Always convert objects to it's json equivalent before setting it as values
     * @return Map of String-Object
     */

    @SerializedName("properties")
    @JsonProperty("properties")
    @Json(name = "properties")
    val properties: ScreenProperties? = null,

    ) : Message(
    EventType.SCREEN,
    messageId,
    context,
    anonymousId,
    userId,
    timestamp,
    channel,
    destinationProps,
    integrations
)

@JsonIgnoreProperties(ignoreUnknown = true, allowSetters = true)
class TrackMessage(
    @JsonProperty("messageId")
    @Json(name = "messageId")
    messageId: String,
    @JsonProperty("context")
    @Json(name = "context")
    context: MessageContext? = null,
    @JsonProperty("anonymousId")
    @Json(name = "anonymousId")
    anonymousId: String,
    @JsonProperty("userId")
    @Json(name = "userId")
    userId: String? = null,
    @JsonProperty("timestamp")
    @Json(name = "timestamp")
    timestamp: String,
    @JsonProperty("channel")
    @Json(name = "channel")
    channel: String? = "",
    @JsonProperty("destinationProps")
    @Json(name = "destinationProps")
    destinationProps: MessageDestinationProps? = null,
    @JsonProperty("integrations")
    @Json(name = "integrations")
    integrations: MessageIntegrations = HashMap(),
    /**
     * @return Name of the event tracked
     */

    @SerializedName("event")
    @JsonProperty("event")
    @Json(name = "event")
    val eventName: String? = null,
    /**
     * Get the properties back as set to the event
     * Always convert objects to it's json equivalent before setting it as values
     * @return Map of String-Object
     */

    @SerializedName("properties")
    @JsonProperty("properties")
    @Json(name = "properties")
    val properties: TrackProperties? = null,
) : Message(
    EventType.TRACK,
    messageId,
    context,
    anonymousId,
    userId,
    timestamp,
    channel,
    destinationProps,
    integrations
){

}



class IdentifyMessage(
    @JsonProperty("messageId")
    @Json(name = "messageId")
    messageId: String,
    @JsonProperty("context")
    @Json(name = "context")
    context: MessageContext? = null,
    @JsonProperty("anonymousId")
    @Json(name = "anonymousId")
    anonymousId: String,
    @JsonProperty("userId")
    @Json(name = "userId")
    userId: String? = null,
    @JsonProperty("timestamp")
    @Json(name = "timestamp")
    timestamp: String,
    @JsonProperty("channel")
    @Json(name = "channel")
    channel: String? = "",
    @JsonProperty("destinationProps")
    @Json(name = "destinationProps")
    destinationProps: MessageDestinationProps? = null,
    @JsonProperty("integrations")
    @Json(name = "integrations")
    integrations: MessageIntegrations = HashMap(),
    /**
     * Get the properties back as set to the event
     * Always convert objects to it's json equivalent before setting it as values
     */

    @SerializedName("properties")
    @JsonProperty("properties")
    @Json(name = "properties")
    val properties: IdentifyProperties? = null,

    ) : Message(
    EventType.IDENTIFY,
    messageId,
    context,
    anonymousId,
    userId,
    timestamp,
    channel,
    destinationProps,
    integrations
)




