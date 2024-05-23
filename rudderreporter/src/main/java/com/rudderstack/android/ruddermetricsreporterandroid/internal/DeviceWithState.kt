package com.rudderstack.android.ruddermetricsreporterandroid.internal

import com.rudderstack.rudderjsonadapter.JsonAdapter
import java.util.Date

/**
 * Stateful information set by the notifier about the device on which the event occurred can be
 * found on this class. These values can be accessed and amended if necessary.
 */
class DeviceWithState internal constructor(
    buildInfo: DeviceBuildInfo,
    jailbroken: Boolean?,
    locale: String?,
    totalMemory: Long?,
    runtimeVersions: MutableMap<String, Any>,

    /**
     * The number of free bytes of storage available on the device
     */
    var freeDisk: Long?,

    /**
     * The number of free bytes of memory available on the device
     */
    var freeMemory: Long?,

    /**
     * The orientation of the device when the event occurred: either portrait or landscape
     */
    var orientation: String?,

    /**
     * The timestamp on the device when the event occurred
     */
    var time: Date? = null,
// private final String timestampString;
) : Device(buildInfo, buildInfo.cpuAbis, jailbroken, locale, totalMemory, runtimeVersions) {
    override fun serialize(jsonAdapter: JsonAdapter): String? {
        return jsonAdapter.writeToJson(this)
    }

    internal override fun toMap(): Map<String, Any?> {
        return super.toMap() + mapOf(
            "freeDisk" to freeDisk.toString(),
            "freeMemory" to freeMemory.toString(),
            "orientation" to orientation.toString(),
            "time" to time?.let { DateUtils.toIso8601(it) },
        )
    }
}