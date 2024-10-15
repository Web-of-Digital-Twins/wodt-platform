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
import application.component.EcosystemRegistry
import application.component.PlatformKnowledgeGraphEngine
import application.presenter.dtd.DigitalTwinDescriptionDeserialization.toDTD
import entity.digitaltwin.DigitalTwinURI
import infrastructure.component.KtorTestingUtility.apiTestApplication
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive

class WoDTDigitalTwinsPlatformInterfaceAPITest : StringSpec({
    val dtUri = DigitalTwinURI("https://example.com/dt")

    val selectQuery = """
        PREFIX healthcare: <https://healthcareontology.com/ontology#>
        PREFIX smartcity: <https://smartcityontology.com/ontology#>

        SELECT ?fuelLevel ?isBusy ?isApproaching
        WHERE {
          <http://localhost:4000/wodt/https://example.com/dt> healthcare:hasFuelLevel ?fuelLevel .
          <http://localhost:4000/wodt/https://example.com/dt> healthcare:isBusy ?isBusy .
          <http://localhost:4000/wodt/https://example.com/dt> smartcity:isApproaching ?isApproaching .
        }
    """.trimIndent()

    fun insertDTD(
        ecosystemRegistry: EcosystemRegistry,
        platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngine,
    ) {
        readResourceFile("wotDtd.json")?.toDTD("application/td+json")?.run {
            ecosystemRegistry.signalRegistration(dtUri)
            platformKnowledgeGraphEngine.mergeDigitalTwinDescription(this)
        }
    }

    fun insertDTKG(
        ecosystemRegistry: EcosystemRegistry,
        platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngine,
    ): String =
        readResourceFile("dtkgWithRelationship.ttl")?.also {
            ecosystemRegistry.signalRegistration(dtUri)
            platformKnowledgeGraphEngine.mergeDigitalTwinKnowledgeGraphUpdate(dtUri, it)
        }.orEmpty()

    """
        An HTTP GET request on the Platform URL should return 303 See Other status code with the Location HTTP
        Header set to the WoDT Digital Twins Platform Knowledge Graph URL, following COOL URI
    """ {
        apiTestApplication { _, _, _ ->
            val clientNoRedirect = createClient {
                followRedirects = false
            }
            val response = clientNoRedirect.get("/")
            response shouldHaveStatus HttpStatusCode.SeeOther
            response.headers[HttpHeaders.Location] shouldBe "/wodt"
        }
    }

    "An HTTP GET request on the Platform Knowledge Graph URL should return the HTTP Status Code No Content if empty" {
        apiTestApplication { _, _, _ ->
            val response = client.get("/wodt")
            response.status shouldBe HttpStatusCode.NoContent
            response.bodyAsText().isEmpty() shouldBe true
        }
    }

    "An HTTP GET request on the Platform Knowledge Graph URL should return the current Platform Knowledge Graph" {
        apiTestApplication { _, ecosystemRegistry, platformKnowledgeGraphEngine ->
            insertDTKG(ecosystemRegistry, platformKnowledgeGraphEngine)
            val response = client.get("/wodt")
            response shouldHaveStatus HttpStatusCode.OK
            response.headers[HttpHeaders.ContentType] shouldBe "text/turtle"
            response.bodyAsText() shouldBe platformKnowledgeGraphEngine.currentPlatformKnowledgeGraph()
        }
    }

    """
        An HTTP GET request for the local Digital Twin Knowledge Graph should return HTTP Status Code Not Found if
        the Digital Twin is not registered
    """ {
        apiTestApplication { _, _, _ ->
            val response = client.get("/wodt/${dtUri.uri}")
            response shouldHaveStatus HttpStatusCode.NotFound
        }
    }

    "An HTTP GET request for the local Digital Twin Knowledge Graph should return the resource if exist" {
        apiTestApplication { _, ecosystemRegistry, platformKnowledgeGraphEngine ->
            insertDTKG(ecosystemRegistry, platformKnowledgeGraphEngine)
            val response = client.get("/wodt/${dtUri.uri}")
            response shouldHaveStatus HttpStatusCode.OK
            response.headers[HttpHeaders.ContentType] shouldBe "text/turtle"
            response.headers[HttpHeaders.Link] shouldBe "<${dtUri.uri}>; rel=\"original\""
            response.bodyAsText() shouldBe readResourceFile("mappedDtkgWithRelationship.ttl")
        }
    }

    "An HTTP POST request for the query should be rejected if it is not specified in the correct content-type" {
        apiTestApplication { _, _, _ ->
            val response = client.post("/wodt/sparql") {
                contentType(ContentType.parse("application/not-supported"))
                setBody(selectQuery)
            }
            response shouldHaveStatus HttpStatusCode.BadRequest
        }
    }

    listOf(
        "application/sparql-results+xml",
        "application/sparql-results+json",
        "text/csv",
        "text/tab-separated-values",
    ).forEach { contentType ->
        "An HTTP POST request for the query (with accept $contentType content type) should be respond correctly" {
            apiTestApplication { _, ecosystemRegistry, platformKnowledgeGraphEngine ->
                insertDTKG(ecosystemRegistry, platformKnowledgeGraphEngine)
                val response = client.post("/wodt/sparql") {
                    contentType(ContentType.parse("application/sparql-query"))
                    accept(ContentType.parse(contentType))
                    setBody(selectQuery)
                }
                response shouldHaveStatus HttpStatusCode.OK
                response.headers[HttpHeaders.ContentType] shouldBe ContentType.parse(contentType).toString()
                response.bodyAsText().isEmpty() shouldBe false
            }
        }
    }

    """
        An HTTP GET request for the directory service should return the HTTP Status No Content if there aren't
        any matching Digital Twins
    """ {
        apiTestApplication { _, _, _ ->
            val response = client.get("/wodt/directory") {
                parameter("pa", "lampPA")
            }
            response shouldHaveStatus HttpStatusCode.NoContent
            Json.decodeFromString<JsonArray>(response.bodyAsText()).isEmpty() shouldBe true
        }
    }

    """
        An HTTP GET request for the directory service should be able to return the registered WoDT Digital Twins
        associated to a Physical Asset
    """ {
        apiTestApplication { _, ecosystemRegistry, platfromKnowledgeGraphEngine ->
            insertDTD(ecosystemRegistry, platfromKnowledgeGraphEngine)
            val response = client.get("/wodt/directory") {
                parameter("pa", "lampPA")
            }
            response shouldHaveStatus HttpStatusCode.OK
            Json.decodeFromString<JsonArray>(response.bodyAsText()).contains(JsonPrimitive(dtUri.uri)) shouldBe true
        }
    }
})
