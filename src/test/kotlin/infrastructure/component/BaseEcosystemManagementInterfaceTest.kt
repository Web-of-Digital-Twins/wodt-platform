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

package infrastructure.component

import TestingUtils.readResourceFile
import entity.digitaltwin.DigitalTwinDescriptorImplementationType
import entity.digitaltwin.DigitalTwinURI
import entity.digitaltwin.WoTDigitalTwinDescriptor
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class BaseEcosystemManagementInterfaceTest : StringSpec({
    val mockEngine = MockEngine { _ ->
        respondOk()
    }
    val httpClient = KtorWoDTPlatformHttpClient(engine = mockEngine, 3000)

    "it should be possible to register a not-registered digital twin" {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(httpClient)
        val dtd = readResourceFile("wotDtd.json").orEmpty()
        runBlocking {
            ecosystemManagementInterface.registerNewDigitalTwin(
                dtd,
                DigitalTwinDescriptorImplementationType.THING_DESCRIPTION.contentType,
                true,
            ) shouldBe true
        }
    }

    "it should not be possible to register an already registered digital twin" {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(httpClient)
        val dtd = readResourceFile("wotDtd.json").orEmpty()
        runBlocking {
            ecosystemManagementInterface.registerNewDigitalTwin(
                dtd,
                DigitalTwinDescriptorImplementationType.THING_DESCRIPTION.contentType,
                true,
            ) shouldBe true
            ecosystemManagementInterface.registerNewDigitalTwin(
                dtd,
                DigitalTwinDescriptorImplementationType.THING_DESCRIPTION.contentType,
                true,
            ) shouldBe false
        }
    }

    "it should be possible to delete a registered digital twin from the ecosystem" {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(httpClient)
        val dtd = readResourceFile("wotDtd.json").orEmpty()
        runBlocking {
            ecosystemManagementInterface.registerNewDigitalTwin(
                dtd,
                DigitalTwinDescriptorImplementationType.THING_DESCRIPTION.contentType,
                true,
            ) shouldBe true
            ecosystemManagementInterface.deleteDigitalTwin(
                WoTDigitalTwinDescriptor.fromJson(
                    Json.decodeFromString<JsonObject>(dtd),
                )?.digitalTwinUri ?: DigitalTwinURI(""),
            ) shouldBe true
        }
    }

    "it should not be possible to delete a not-registered digital twin from the ecosystem" {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(httpClient)
        runBlocking {
            ecosystemManagementInterface.deleteDigitalTwin(
                DigitalTwinURI("http://not-registered.it"),
            ) shouldBe false
        }
    }
})
