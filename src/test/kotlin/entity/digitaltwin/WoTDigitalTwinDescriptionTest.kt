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

import TestingUtils.readResourceFile
import entity.ontology.WoDTVocabulary
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class WoTDigitalTwinDescriptionTest : StringSpec({
    fun testIncompleteWoTDTD(keyToRemove: String) = readResourceFile("wotDtd.json")?.let {
        val jsonDtd = JsonObject(Json.decodeFromString<JsonObject>(it).toMutableMap().apply { remove(keyToRemove) })
        val dtd = WoTDigitalTwinDescription.fromJson(jsonDtd)
        dtd shouldBe null
    }

    fun testIncompleteWoTDTDFromFile(file: String) = readResourceFile(file)?.let {
        val jsonDtd = Json.decodeFromString<JsonObject>(it)
        val dtd = WoTDigitalTwinDescription.fromJson(jsonDtd)
        dtd shouldBe null
    }

    "it should be possible to obtain the necessary information from a correct wot dtd implementation" {
        readResourceFile("wotDtd.json")?.let {
            val jsonDtd = Json.decodeFromString<JsonObject>(it)
            val dtd = WoTDigitalTwinDescription.fromJson(jsonDtd)
            dtd shouldNotBe null
            dtd?.physicalAssetId shouldBe "lampPA"
            dtd?.digitalTwinUri shouldBe DigitalTwinURI("https://example.com/dt")
            dtd?.obtainObservationForm() shouldBe Form("ws://localhost:3000/dtkg", FormProtocol.WEBSOCKET)
            jsonDtd shouldBe Json.decodeFromString<JsonObject>(dtd?.obtainRepresentation().orEmpty())
        }
    }

    "if the physical asset id is not present then it can't be built" {
        testIncompleteWoTDTD(WoDTVocabulary.PHYSICAL_ASSET_ID)
    }

    "if the digital twin uri is not present then it can't be built" {
        testIncompleteWoTDTD("id")
    }

    "if the Thing-level forms are not present then it can't be built" {
        testIncompleteWoTDTD("forms")
    }

    "if the observeallproperties Thing-level form is not present then it can't be built" {
        testIncompleteWoTDTDFromFile("wotDtdWithoutObservation.json")
    }

    "if the observation subprotocol is not supported then it can't be built" {
        readResourceFile("wotDtdWithoutSupportedObservation.json")?.let {
            val jsonDtd = Json.decodeFromString<JsonObject>(it)
            val dtd = WoTDigitalTwinDescription.fromJson(jsonDtd)
            dtd shouldBe null
        }
    }
})
