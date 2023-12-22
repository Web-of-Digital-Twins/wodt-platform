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

import application.component.EcosystemManagementInterface
import application.component.PlatformKnowledgeGraphEngine
import application.component.WoDTDigitalTwinsObserver
import application.component.WoDTPlatformWebServer
import entity.event.DigitalTwinDeleted
import entity.event.NewDigitalTwinRegistered
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * The Engine that runs the WoDT Digital Twins Platform connecting all the events from components.
 */
class WoDTPlatformEngine(
    private val ecosystemManagementInterface: EcosystemManagementInterface,
    private val woDTDigitalTwinsObserver: WoDTDigitalTwinsObserver,
    private val platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngine,
    private val platformWebServer: WoDTPlatformWebServer,
) {
    /**
     * Method to start the [WoDTPlatformEngine].
     */
    suspend fun start() = coroutineScope {
        launch {
            ecosystemManagementInterface.ecosystemEvents.collect {
                if (it is NewDigitalTwinRegistered) {
                    launch { woDTDigitalTwinsObserver.observeDigitalTwin(it.dtd) }
                    platformKnowledgeGraphEngine.mergeDigitalTwinDescriptor(it.dtd)
                } else if (it is DigitalTwinDeleted) {
                    woDTDigitalTwinsObserver.stopObservationOfDigitalTwin(it.dtURI)
                    platformKnowledgeGraphEngine.deleteDigitalTwin(it.dtURI)
                }
            }
        }
        launch {
            woDTDigitalTwinsObserver.dtkgRawEvents.collect {
                platformKnowledgeGraphEngine.mergeDigitalTwinKnowledgeGraphUpdate(it.first, it.second)
            }
        }
        launch {
            platformKnowledgeGraphEngine.platformKnowledgeGraphs.collect {
                println("--".repeat(10))
                println(it)
            }
        }
        platformWebServer.start()
    }
}
