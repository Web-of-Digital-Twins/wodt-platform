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
 * This interface models the Ecosystem Registry component of the Abstract Architecture.
 */
interface EcosystemRegistry : EcosystemRegistryDeletionSignaler {
    /** Signal the registration of a new Digital Twin to the Platform. */
    fun signalRegistration(digitalTwinUri: DigitalTwinURI)

    /** Get all the registered Digital Twins. */
    fun getRegisteredDigitalTwins(): Set<DigitalTwinURI>
}
