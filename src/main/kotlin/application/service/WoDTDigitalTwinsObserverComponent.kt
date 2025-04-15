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

import application.component.EcosystemRegistryDeletionSignaler
import application.component.WoDTDigitalTwinsObserver
import application.component.WoDTDigitalTwinsObserverWsClient
import entity.digitaltwin.DigitalTwinDescription
import entity.digitaltwin.DigitalTwinURI
import entity.digitaltwin.FormProtocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * The implementation of the [WoDTDigitalTwinsObserver] component.
 * It uses a [wsClient] to manage the observation of WoDT Digital Twins.
 */
class WoDTDigitalTwinsObserverComponent(
    private val ecosystemRegistry: EcosystemRegistryDeletionSignaler,
    private val wsClient: WoDTDigitalTwinsObserverWsClient,
) : WoDTDigitalTwinsObserver {

    private var _dtkgFlowMap: Map<DigitalTwinURI, MutableSharedFlow<String>> = mapOf()

    override val dtkgRawEventsMap: Map<DigitalTwinURI, Flow<String>> = this._dtkgFlowMap

    override suspend fun observeDigitalTwin(dtd: DigitalTwinDescription) {
        if (dtd.obtainObservationForm().protocol == FormProtocol.WEBSOCKET) {
            this._dtkgFlowMap += (dtd.digitalTwinUri to MutableSharedFlow())
            this.wsClient.observeDigitalTwin(
                dtd.obtainObservationForm().href,
                { _dtkgFlowMap[dtd.digitalTwinUri]?.emit(it) },
                /* TODO why remove the DT if the socket is closed?
                    Couldn't this be a network issue e.g. DT is down?
                    The DT should still be part of the ecosystem until reconnection
                    In that case I expect the DT to send a DTD update when it is back up...
                 */
                { ecosystemRegistry.signalDeletion(dtd.digitalTwinUri) },

            )
        }
    }

    override suspend fun stopObservationOfDigitalTwin(dtUri: DigitalTwinURI) {
        this.wsClient.stopObservationOfDigitalTwin(dtUri.uri)
        this._dtkgFlowMap -= dtUri // TODO check
    }
}
