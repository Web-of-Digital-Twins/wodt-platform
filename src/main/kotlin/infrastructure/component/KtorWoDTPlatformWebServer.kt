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

import application.component.EcosystemManagementInterface
import application.component.PlatformKnowledgeGraphEngineReader
import application.component.WoDTPlatformWebServer
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.websocket.WebSockets

/**
 * Implementation of the [WoDTPlatformWebServer].
 */
class KtorWoDTPlatformWebServer(
    private val ecosystemManagementInterface: EcosystemManagementInterface,
    private val platformKnowledgeGraphEngineReader: PlatformKnowledgeGraphEngineReader,
    private val exposedPort: Int,
) : WoDTPlatformWebServer {

    override fun start() {
        embeddedServer(Netty, port = this.exposedPort) {
            install(WebSockets)
            dispatcher(this)
        }.start(wait = false)
    }

    private fun dispatcher(app: Application) {
        with(app) {
            ecosystemManagementAPI(ecosystemManagementInterface)
            wodtDigitalTwinsPlatformInterfaceAPI(platformKnowledgeGraphEngineReader)
        }
    }
}
