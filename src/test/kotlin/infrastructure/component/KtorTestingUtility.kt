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

import application.component.EcosystemRegistry
import application.component.PlatformKnowledgeGraphEngine
import application.service.BaseEcosystemManagementInterface
import application.service.EcosystemRegistryService
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import java.net.URI

object KtorTestingUtility {
    private val platformExposedUrl = URI.create("http://localhost:4000")
    private val mockEngine = MockEngine { _ -> respondOk() }
    private val httpClient = KtorWoDTPlatformHttpClient(platformExposedUrl, engine = mockEngine)

    fun apiTestApplication(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        tests: suspend ApplicationTestBuilder.(
            ecosystemManagementInterface: BaseEcosystemManagementInterface,
            ecosystemRegistry: EcosystemRegistry,
            platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngine,
        ) -> Unit,
    ) {
        val ecosystemManagementInterface = BaseEcosystemManagementInterface(
            EcosystemRegistryService(platformExposedUrl),
            httpClient,
        )
        val ecosystemRegistry = EcosystemRegistryService(platformExposedUrl)
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
        testApplication {
            install(WebSockets)
            application {
                ecosystemManagementAPI(ecosystemManagementInterface)
                wodtDigitalTwinsPlatformInterfaceAPI(platformKnowledgeGraphEngine, ecosystemRegistry)
            }
            runTest {
                val job = launch(dispatcher) { platformKnowledgeGraphEngine.start() }
                tests(ecosystemManagementInterface, ecosystemRegistry, platformKnowledgeGraphEngine)
                job.cancel()
            }
        }
    }
}
