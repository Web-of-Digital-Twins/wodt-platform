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

package application.component

import entity.digitaltwin.DigitalTwinURI

/**
 * This interface models a client of the [EcosystemRegistry] that can only signal the deletion of a Digital Twin.
 */
interface EcosystemRegistryDeletionSignaler {
    /** Signal the deletion of a Digital Twin from the platform. */
    fun signalDeletion(digitalTwinURI: DigitalTwinURI)
}

/**
 * This interface models the mapping part of the [EcosystemRegistry] to be used to obtain the uri/url mappings
 * of WoDT Digital Twins.
 */
interface EcosystemRegistryMapper {
    /** Obtain the mapped local url from [digitalTwinUri]. It returns null if the Digital Twin is not registered. */
    fun getLocalUrl(digitalTwinUri: DigitalTwinURI): String?

    /** Obtain the WoDT Digital Twin URI from a [localDigitalTwinUrl]. It returns null if it is not registered. */
    fun getDigitalTwinUri(localDigitalTwinUrl: String): DigitalTwinURI?
}

/**
 * This interface models the catalog part of the ecosystem registry that maintains the catalog of the registered
 * Digital Twins.
 */
interface EcosystemRegistryCatalog {
    /** Get all the registered Digital Twins. */
    fun getRegisteredDigitalTwins(): Set<DigitalTwinURI>
}

/**
 * This interface models the Ecosystem Registry component of the Abstract Architecture.
 */
interface EcosystemRegistry : EcosystemRegistryDeletionSignaler, EcosystemRegistryMapper, EcosystemRegistryCatalog {
    /** Signal the registration of a new Digital Twin to the Platform. */
    fun signalRegistration(digitalTwinUri: DigitalTwinURI)
}
