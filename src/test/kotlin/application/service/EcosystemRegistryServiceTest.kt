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

import entity.digitaltwin.DigitalTwinURI
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EcosystemRegistryServiceTest : StringSpec({
    val testPort = 3000
    val digitalTwinUri = DigitalTwinURI("http://example.it")

    "initial size of the registry should be 0" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        ecosystemRegistry.getRegisteredDigitalTwins().size shouldBe 0
    }

    "it should be possible to add a Digital Twin" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        ecosystemRegistry.signalRegistration(digitalTwinUri)

        ecosystemRegistry.getRegisteredDigitalTwins().contains(digitalTwinUri) shouldBe true
        ecosystemRegistry.getRegisteredDigitalTwins().size shouldBe 1
    }

    "it should be possible to delete a Digital Twin" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        ecosystemRegistry.signalRegistration(digitalTwinUri)
        ecosystemRegistry.signalDeletion(digitalTwinUri)

        ecosystemRegistry.getRegisteredDigitalTwins().contains(digitalTwinUri) shouldBe false
        ecosystemRegistry.getRegisteredDigitalTwins().size shouldBe 0
    }

    "it should be possible to get the mapped local url of a registered WoDT Digital Twin" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        ecosystemRegistry.signalRegistration(digitalTwinUri)

        ecosystemRegistry.getLocalUrl(digitalTwinUri) shouldBe "http://localhost:$testPort/wodt/${digitalTwinUri.uri}"
    }

    "it should not be possible to obtain the mapped local url of a not-registered Digital Twin" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)

        ecosystemRegistry.getLocalUrl(digitalTwinUri) shouldBe null
    }

    "it should be possible to obtain the WoDT Digital Twin URI from a mapped local url of a registered Digital Twin" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        ecosystemRegistry.signalRegistration(digitalTwinUri)

        ecosystemRegistry.getDigitalTwinUri("http://localhost:$testPort/wodt/${digitalTwinUri.uri}") shouldBe digitalTwinUri
    }

    "it should not be possible to obtain the WoDT Digital Twin URI from a local url of a not-registered Digital Twin" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)

        ecosystemRegistry.getDigitalTwinUri("http://localhost:$testPort/wodt/${digitalTwinUri.uri}") shouldBe null
    }
})
