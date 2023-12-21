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
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

/**
 * Implementation of the [WoDTPlatformHttpClient] for the Platform.
 */
class KtorWoDTPlatformHttpClient(
    engine: HttpClientEngine = CIO.create(),
    exposedPort: Int? = null,
) : EcosystemManagementHttpClient, WoDTDigitalTwinsObserverWsClient {
    private val webSockets: MutableMap<String, DefaultClientWebSocketSession> = mutableMapOf()
    private val httpClient = HttpClient(engine) {
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

    companion object {
        private const val EXPOSED_PORT_VARIABLE = "EXPOSED_PORT"
    }
}
