/*
 * Copyright (c) 2023. Andrea Giulianelli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package application.presenter.dtd

import entity.digitaltwin.DigitalTwinDescriptor
import entity.digitaltwin.WoTDigitalTwinDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * Module that wraps the deserialization utilities for the Digital Twin Descriptor.
 */
object DigitalTwinDescriptorDeserialization {
    /** Obtain the [DigitalTwinDescriptor] from a String expressed in a [contentType]. */
    fun String.toDTD(contentType: String) = when (contentType) {
        "application/td+json" -> WoTDigitalTwinDescriptor.fromJson(Json.decodeFromString<JsonObject>(this))
        else -> null
    }
}
