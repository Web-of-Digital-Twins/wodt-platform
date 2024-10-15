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

package application.service

import TestingUtils.readResourceFile
import entity.digitaltwin.DigitalTwinDescriptionImplementationType
import entity.digitaltwin.DigitalTwinURI
import entity.digitaltwin.WoTDigitalTwinDescription
import infrastructure.component.KtorWoDTPlatformHttpClient
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.net.URI

class BaseEcosystemManagementInterfaceTest : StringSpec({
    val mockEngine = MockEngine { _ ->
        respondOk()
    }
    val platformExposedUrl = URI.create("http://localhost:4000")
    val httpClient = KtorWoDTPlatformHttpClient(platformExposedUrl, engine = mockEngine)

    "it should be possible to register a not-registered digital twin" {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(
            EcosystemRegistryService(platformExposedUrl),
            httpClient,
        )
        val dtd = readResourceFile("wotDtd.json").orEmpty()
        runBlocking {
            ecosystemManagementInterface.registerNewDigitalTwin(
                dtd,
                DigitalTwinDescriptionImplementationType.THING_DESCRIPTION.contentType,
                true,
            ) shouldBe true
        }
    }

    "it should not be possible to register an already registered digital twin" {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(
            EcosystemRegistryService(platformExposedUrl),
            httpClient,
        )
        val dtd = readResourceFile("wotDtd.json").orEmpty()
        runBlocking {
            ecosystemManagementInterface.registerNewDigitalTwin(
                dtd,
                DigitalTwinDescriptionImplementationType.THING_DESCRIPTION.contentType,
                true,
            ) shouldBe true
            ecosystemManagementInterface.registerNewDigitalTwin(
                dtd,
                DigitalTwinDescriptionImplementationType.THING_DESCRIPTION.contentType,
                true,
            ) shouldBe false
        }
    }

    "it should be possible to delete a registered digital twin from the ecosystem" {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(
            EcosystemRegistryService(platformExposedUrl),
            httpClient,
        )
        val dtd = readResourceFile("wotDtd.json").orEmpty()
        runBlocking {
            ecosystemManagementInterface.registerNewDigitalTwin(
                dtd,
                DigitalTwinDescriptionImplementationType.THING_DESCRIPTION.contentType,
                true,
            ) shouldBe true
            ecosystemManagementInterface.deleteDigitalTwin(
                WoTDigitalTwinDescription.fromJson(
                    Json.decodeFromString<JsonObject>(dtd),
                )?.digitalTwinUri ?: DigitalTwinURI(""),
            ) shouldBe true
        }
    }

    "it should not be possible to delete a not-registered digital twin from the ecosystem" {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(
            EcosystemRegistryService(platformExposedUrl),
            httpClient,
        )
        runBlocking {
            ecosystemManagementInterface.deleteDigitalTwin(
                DigitalTwinURI("http://not-registered.it"),
            ) shouldBe false
        }
    }
})
