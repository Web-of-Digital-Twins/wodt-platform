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

import application.component.EcosystemRegistryCatalog
import application.component.PlatformKnowledgeGraphEngineReader
import entity.digitaltwin.DigitalTwinURI
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.contentType
import io.ktor.server.request.header
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.send
import kotlinx.serialization.json.Json

/**
 * The WoDT Platform Interface API available to handle requests of Consumers.
 */
fun Application.wodtDigitalTwinsPlatformInterfaceAPI(
    platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngineReader,
    ecosystemRegistryCatalog: EcosystemRegistryCatalog,
) {
    routing {
        getPlatform()
        getCompletePlatformKnowledgeGraph(platformKnowledgeGraphEngine)
        getLocalDigitalTwinKnowledgeGraph(platformKnowledgeGraphEngine)
        queryOnPlatformKnowledgeGraph(platformKnowledgeGraphEngine)
        observePlatformKnowledgeGraph(platformKnowledgeGraphEngine)
        observeDigitalTwinKnowledgeGraph(platformKnowledgeGraphEngine)
        getDigitalTwinsFromPhysicalAssetId(platformKnowledgeGraphEngine)
        getDigitalTwins(ecosystemRegistryCatalog)
    }
}

private fun Route.getPlatform() =
    get("/") {
        call.response.headers.append(HttpHeaders.Location, "/wodt")
        call.respond(HttpStatusCode.SeeOther)
    }

private fun Route.getCompletePlatformKnowledgeGraph(platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngineReader) =
    get("/wodt") {
        platformKnowledgeGraphEngine.currentPlatformKnowledgeGraph().apply {
            when (this) {
                null -> call.respond(HttpStatusCode.NoContent)
                else -> {
                    call.response.status(
                        if (this.isEmpty() || this.isBlank()) { HttpStatusCode.NoContent } else { HttpStatusCode.OK },
                    )
                    call.response.headers.append(HttpHeaders.ContentType, "text/turtle")
                    call.respond(this)
                }
            }
        }
    }

private fun Route.getLocalDigitalTwinKnowledgeGraph(platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngineReader) =
    get("/wodt/{dtUri...}") {
        call.parameters.getAll("dtUri")?.also { pathParameters ->
            if (pathParameters.size >= 2) {
                val dtUri = obtainDigitalTwinUriFromPathParameters(pathParameters)
                platformKnowledgeGraphEngine.currentCachedDigitalTwinKnowledgeGraph(dtUri).apply {
                    when (this) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            call.response.status(
                                if (this.isEmpty() || this.isBlank()) {
                                    HttpStatusCode.NoContent
                                } else { HttpStatusCode.OK },
                            )
                            call.response.headers.append(HttpHeaders.ContentType, "text/turtle")
                            call.response.headers.append(HttpHeaders.Link, "<$dtUri>; rel=\"original\"")
                            call.respond(this)
                        }
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }

private fun Route.queryOnPlatformKnowledgeGraph(platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngineReader) =
    post("/wodt/sparql") {
        val acceptedContentType = call.request.header(HttpHeaders.Accept).toString()
        if (call.request.contentType().toString() == "application/sparql-query") {
            platformKnowledgeGraphEngine.query(call.receiveText(), acceptedContentType).apply {
                when (this) {
                    null -> call.respond(HttpStatusCode.BadRequest)
                    else -> {
                        call.response.headers.append(HttpHeaders.ContentType, acceptedContentType)
                        call.respond(HttpStatusCode.OK, this)
                    }
                }
            }
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

private fun Route.observePlatformKnowledgeGraph(platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngineReader) =
    webSocket("/wodt") {
        platformKnowledgeGraphEngine.platformKnowledgeGraphs.collect {
            send(it)
            KotlinLogging.logger {}.info { "[HWoDT logging] OUTGOING PLATFORM KG" }
        }
    }

private fun Route.observeDigitalTwinKnowledgeGraph(platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngineReader) =
    webSocket("/wodt/{dtUri...}") {
        call.parameters.getAll("dtUri")?.also { pathParameters ->
            if (pathParameters.size >= 2) {
                val dtUri = obtainDigitalTwinUriFromPathParameters(pathParameters)
                send(platformKnowledgeGraphEngine.currentCachedDigitalTwinKnowledgeGraph(dtUri).orEmpty())
                platformKnowledgeGraphEngine.currentCachedDigitalTwinKnowledgeGraphUpdates(dtUri)?.collect {
                    send(it)
                    KotlinLogging.logger {}.info { "[HWoDT logging] OUTGOING DTKG of: $dtUri" }
                }
            }
        }
    }

private fun Route.getDigitalTwinsFromPhysicalAssetId(platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngineReader) =
    get("/wodt/directory") {
        val paId = call.request.queryParameters["pa"]
        if (paId != null) {
            call.respondWithJsonArray(platformKnowledgeGraphEngine.getDigitalTwinsFromPhysicalAsset(paId))
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

private fun Route.getDigitalTwins(ecosystemRegistryCatalog: EcosystemRegistryCatalog) =
    get("/wodt/dts") {
        call.respondWithJsonArray(ecosystemRegistryCatalog.getRegisteredDigitalTwins())
    }

private suspend fun ApplicationCall.respondWithJsonArray(dtUris: Set<DigitalTwinURI>) {
    this.response.status(if (dtUris.isEmpty()) { HttpStatusCode.NoContent } else { HttpStatusCode.OK })
    this.response.headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    this.respond(Json.encodeToString(dtUris.map { it.uri }))
}
