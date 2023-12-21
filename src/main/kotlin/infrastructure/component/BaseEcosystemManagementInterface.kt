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
import application.component.WoDTPlatformHttpClient
import application.presenter.dtd.DigitalTwinDescriptorDeserialization.toDTD
import entity.digitaltwin.DigitalTwinDescriptorImplementationType
import entity.digitaltwin.DigitalTwinURI
import entity.event.DigitalTwinDeleted
import entity.event.EcosystemEvent
import entity.event.NewDigitalTwinRegistered
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * This class provides an implementation of the [EcosystemManagementInterface] component.
 */
class BaseEcosystemManagementInterface(
    private val httpClient: WoDTPlatformHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : EcosystemManagementInterface {
    private val _ecosystemEvents = MutableSharedFlow<EcosystemEvent>()
    private var registeredDigitalTwins: Set<DigitalTwinURI> = setOf()

    override val ecosystemEvents = this._ecosystemEvents.asSharedFlow()

    override suspend fun registerNewDigitalTwin(rawDtd: String, contentType: String, shouldNotify: Boolean): Boolean =
        coroutineScope {
            if (supportedContentTypes.contains(contentType)) {
                val dtd = rawDtd.toDTD(contentType)
                dtd?.let {
                    if (!registeredDigitalTwins.contains(dtd.digitalTwinUri)) {
                        registeredDigitalTwins = registeredDigitalTwins + dtd.digitalTwinUri
                        _ecosystemEvents.emit(NewDigitalTwinRegistered(dtd))
                        if (shouldNotify) {
                            httpClient.sendRegistrationNotification(dtd.digitalTwinUri.uri)
                        } else { true }
                    } else {
                        false
                    }
                } ?: false
            } else {
                false
            }
        }

    override fun deleteDigitalTwin(dtUri: DigitalTwinURI): Boolean = if (registeredDigitalTwins.contains(dtUri)) {
        CoroutineScope(this.dispatcher).launch { _ecosystemEvents.emit(DigitalTwinDeleted(dtUri)) }
        true
    } else {
        false
    }

    companion object {
        private val supportedContentTypes = DigitalTwinDescriptorImplementationType.entries.map { it.contentType }
    }
}
