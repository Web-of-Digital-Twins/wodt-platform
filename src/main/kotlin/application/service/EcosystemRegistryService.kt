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

import application.component.EcosystemRegistry
import entity.digitaltwin.DigitalTwinURI
import utils.UriUtil.relativeResolve
import java.net.URI
import java.util.Collections

/**
 * This class models the Ecosystem Registry component of the Abstract Architecture.
 */
class EcosystemRegistryService(private val platformExposedUrl: URI) : EcosystemRegistry {
    private var registeredDigitalTwins: Set<DigitalTwinURI> = Collections.synchronizedSet(setOf())

    override fun signalRegistration(digitalTwinUri: DigitalTwinURI) {
        registeredDigitalTwins = Collections.synchronizedSet(registeredDigitalTwins + digitalTwinUri)
    }

    override fun getRegisteredDigitalTwins(): Set<DigitalTwinURI> = registeredDigitalTwins

    override fun signalDeletion(digitalTwinURI: DigitalTwinURI) {
        registeredDigitalTwins = Collections.synchronizedSet(registeredDigitalTwins - digitalTwinURI)
    }

    override fun getLocalUrl(digitalTwinUri: DigitalTwinURI): String? =
        if (registeredDigitalTwins.contains(digitalTwinUri)) {
            this.platformExposedUrl.relativeResolve("/wodt/${digitalTwinUri.uri}").toString()
        } else {
            null
        }

    override fun getDigitalTwinUri(localDigitalTwinUrl: String): DigitalTwinURI? =
        with(
            DigitalTwinURI(
                localDigitalTwinUrl.removePrefix(this.platformExposedUrl.relativeResolve("/wodt/").toString()),
            ),
        ) {
            if (registeredDigitalTwins.contains(this)) {
                this
            } else {
                null
            }
        }
}
