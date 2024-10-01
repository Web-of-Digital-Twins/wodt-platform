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

import application.component.EcosystemManagementHttpClient
import application.component.WoDTDigitalTwinsObserverWsClient
import application.presenter.api.PlatformRegistration
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException

/**
 * Implementation of the Http and Ws client for the Platform.
 */
class KtorWoDTPlatformHttpClient(
    engine: HttpClientEngine = CIO.create(),
    exposedPort: Int? = null,
) : EcosystemManagementHttpClient, WoDTDigitalTwinsObserverWsClient {
    private val webSockets: MutableMap<String, DefaultClientWebSocketSession> = mutableMapOf()
    private val httpClient = HttpClient(engine) {
        install(HttpRequestRetry) {
            exponentialDelay()
            maxRetries = 5
        }
        install(WebSockets) {
            pingInterval = WEBSOCKET_PING_INTERVAL
        }
        install(ContentNegotiation) {
            json()
        }
    }
    private val exposedPort: Int

    init {
        if (exposedPort == null) {
            checkNotNull(System.getenv(EXPOSED_PORT_VARIABLE)) { "Please provide the exposed port" }
        }
        this.exposedPort = exposedPort ?: System.getenv(EXPOSED_PORT_VARIABLE).toInt()
    }

    override suspend fun sendRegistrationNotification(dtUri: String) = this.httpClient.post(dtUri) {
        contentType(ContentType.Application.Json)
        setBody(PlatformRegistration("http://localhost:${System.getenv(EXPOSED_PORT_VARIABLE)}"))
        url { appendPathSegments("platform") }
    }.status == HttpStatusCode.OK

    override suspend fun observeDigitalTwin(
        dtUri: String,
        onData: suspend (String) -> Unit,
        onClose: suspend () -> Unit,
    ) {
        try {
            this.httpClient.webSocket(dtUri) {
                webSockets[dtUri] = this
                while (true) {
                    val incomingData = incoming.receive()
                    if (incomingData is Frame.Text) {
                        onData(incomingData.readText())
                    } else if (incomingData is Frame.Close) {
                        onClose()
                    }
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            // websocket close unexpectedly
            logger.info { e.message }
            webSockets.remove(dtUri)
            onClose()
        }
    }

    override suspend fun stopObservationOfDigitalTwin(dtUri: String) {
        webSockets[dtUri]?.close()
        webSockets.remove(dtUri)
    }

    companion object {
        private const val EXPOSED_PORT_VARIABLE = "EXPOSED_PORT"
        private const val WEBSOCKET_PING_INTERVAL = 1_000L
        private val logger = KotlinLogging.logger {}
    }
}
