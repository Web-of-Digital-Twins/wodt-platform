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

package entity.digitaltwin

import entity.ontology.WoDTVocabulary
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Web of Things compliant [DigitalTwinDescription] implementation.
 */
class WoTDigitalTwinDescription private constructor(
    override val physicalAssetId: String,
    override val digitalTwinUri: DigitalTwinURI,
    private val observationForm: Form,
    private val representation: String,
) : DigitalTwinDescription {
    override val implementationType = DigitalTwinDescriptionImplementationType.THING_DESCRIPTION

    override fun obtainObservationForm(): Form = this.observationForm

    override fun obtainRepresentation(): String = this.representation

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WoTDigitalTwinDescription
        return Json.decodeFromString<JsonObject>(representation) == Json.decodeFromString<JsonObject>(
            other.representation,
        )
    }

    override fun hashCode(): Int = Json.decodeFromString<JsonObject>(representation).hashCode()

    companion object {
        private const val DIGITAL_TWIN_URI_FIELD = "id"
        private const val PROPERTIES_LIST = "properties"
        private const val SNAPSHOT_PROPERTY = "snapshot"
        private const val FORM_LIST = "forms"
        private const val FORM_SUBPROTOCOL = "subprotocol"
        private const val FORM_OP = "op"
        private const val FORM_OP_OBSERVE_PROPERTY = "observeproperty"
        private const val FORM_HREF = "href"

        /**
         * Create a [WoTDigitalTwinDescription] from its JSON [rawDTD] representation.
         */
        fun fromJson(rawDTD: JsonObject): WoTDigitalTwinDescription? {
            val paId = rawDTD[WoDTVocabulary.PHYSICAL_ASSET_ID]?.jsonPrimitive?.content
            val dtUri = rawDTD[DIGITAL_TWIN_URI_FIELD]?.jsonPrimitive?.content?.let { DigitalTwinURI(it) }
            val snapshotForm = rawDTD[PROPERTIES_LIST]
                ?.jsonObject
                ?.get(SNAPSHOT_PROPERTY)
                ?.jsonObject
                ?.get(FORM_LIST)
                ?.jsonArray
                ?.find {
                    it.jsonObject[FORM_OP]?.jsonArray?.contains(JsonPrimitive(FORM_OP_OBSERVE_PROPERTY)) == true &&
                        it.jsonObject[FORM_SUBPROTOCOL]?.jsonPrimitive?.content == FormProtocol.WEBSOCKET.protocolName
                }
                ?.jsonObject
                ?.let { form ->
                    form[FORM_HREF]?.jsonPrimitive?.content?.let { href -> Form(href, FormProtocol.WEBSOCKET) }
                }
            return if (paId != null && dtUri != null && snapshotForm != null) {
                WoTDigitalTwinDescription(paId, dtUri, snapshotForm, rawDTD.toString())
            } else {
                null
            }
        }
    }
}
