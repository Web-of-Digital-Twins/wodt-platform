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
import entity.event.EcosystemEvent
import kotlinx.coroutines.flow.Flow

/**
 * This interface models the Ecosystem Management Interface component of the Abstract Architecture.
 */
interface EcosystemManagementInterface {
    /** Obtain the flow of ecosystem events, due to registration and deletion of the Digital Twins to the Platform. */
    val ecosystemEvents: Flow<EcosystemEvent>

    /**
     * Registration of a new Digital Twin to the WoDT Platform.
     * It needs the [rawDtd] and its [contentType] in order to be able to understand and validate it.
     * Moreover, if it [shouldNotify] the WoDT Digital Twin about the correct registration it will send the request
     * to the new registered WoDT Digital Twin.
     * @return true if the Digital Twin is correctly registered, false instead.
     */
    suspend fun registerNewDigitalTwin(rawDtd: String, contentType: String, shouldNotify: Boolean): Boolean

    /**
     * A registered WoDT Digital Twin, identified by its [dtUri], signals its deletion from the ecosystem.
     * @return true if correctly deleted, false if it isn't registered to the platform
     */
    fun deleteDigitalTwin(dtUri: DigitalTwinURI): Boolean

    //TODO missing the update of a DTD?
}
