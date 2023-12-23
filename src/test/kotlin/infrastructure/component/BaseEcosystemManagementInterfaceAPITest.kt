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
import application.presenter.dtd.DigitalTwinDescriptorDeserialization.toDTD
import infrastructure.component.KtorTestingUtility.apiTestApplication
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class BaseEcosystemManagementInterfaceAPITest : StringSpec({
    val dtd = readResourceFile("wotDtd.json").orEmpty()
    val contentType = "application/td+json"

    "It should be possible to request the registration of a Digital Twin" {
        apiTestApplication { ecosystemManagementInterface, _, _ ->
            val response = client.post("/wodt") {
                contentType(ContentType.parse(contentType))
                setBody(dtd)
            }
            response.status shouldBe HttpStatusCode.Accepted
            // ok registered
            ecosystemManagementInterface.registerNewDigitalTwin(dtd, contentType, false) shouldBe false
        }
    }

    "If someone try to register with a malformed dtd the api should respond properly" {
        apiTestApplication { ecosystemManagementInterface, _, _ ->
            val response = client.post("/wodt") {
                contentType(ContentType.parse(contentType))
                setBody("{}")
            }
            response.status shouldBe HttpStatusCode.BadRequest
            // ok it wasn't registered
            ecosystemManagementInterface.registerNewDigitalTwin(dtd, contentType, false) shouldBe true
        }
    }

    "If someone try to register an already registered Digital Twin, the api should respond properly" {
        apiTestApplication { ecosystemManagementInterface, _, _ ->
            ecosystemManagementInterface.registerNewDigitalTwin(dtd, contentType, false)
            val response = client.post("/wodt") {
                contentType(ContentType.parse(contentType))
                setBody(dtd)
            }
            response.status shouldBe HttpStatusCode.Conflict
        }
    }

    "It should be possible to delete a registered Digital Twin" {
        apiTestApplication { ecosystemManagementInterface, _, _ ->
            ecosystemManagementInterface.registerNewDigitalTwin(dtd, contentType, false)
            val response = client.delete("/wodt/${dtd.toDTD(contentType)?.digitalTwinUri?.uri.orEmpty()}")
            response.status shouldBe HttpStatusCode.Accepted
        }
    }

    "If someone try to delete a Digital Twin that is not registered, the api should respond properly" {
        apiTestApplication { _, _, _ ->
            val response = client.delete("/wodt/${dtd.toDTD(contentType)?.digitalTwinUri?.uri.orEmpty()}")
            response.status shouldBe HttpStatusCode.NotFound
        }
    }
})
