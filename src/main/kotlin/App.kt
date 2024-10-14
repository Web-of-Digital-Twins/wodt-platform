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

import application.service.BaseEcosystemManagementInterface
import application.service.EcosystemRegistryService
import application.service.WoDTDigitalTwinsObserverComponent
import application.service.WoDTPlatformEngine
import infrastructure.component.JenaPlatformKnowledgeGraphEngine
import infrastructure.component.KtorWoDTPlatformHttpClient
import infrastructure.component.KtorWoDTPlatformWebServer
import kotlinx.coroutines.runBlocking

/**
 * Function to start the WoDT Platform.
 */
fun main(): Unit = runBlocking {
    val platformHttpClient = KtorWoDTPlatformHttpClient()
    val ecosystemRegistry = EcosystemRegistryService()
    val ecosystemManagementInterface = BaseEcosystemManagementInterface(ecosystemRegistry, platformHttpClient)
    val woDTDigitalTwinsObserver = WoDTDigitalTwinsObserverComponent(ecosystemRegistry, platformHttpClient)
    val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
    val platformWebServer = KtorWoDTPlatformWebServer(ecosystemManagementInterface, platformKnowledgeGraphEngine)

    WoDTPlatformEngine(
        ecosystemManagementInterface,
        woDTDigitalTwinsObserver,
        platformKnowledgeGraphEngine,
        platformWebServer,
    ).start()
}
