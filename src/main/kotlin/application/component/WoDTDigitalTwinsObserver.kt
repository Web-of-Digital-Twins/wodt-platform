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

import entity.digitaltwin.DigitalTwinDescription
import entity.digitaltwin.DigitalTwinURI
import kotlinx.coroutines.flow.Flow

/**
 * This interface models the WoDT Digital Twins Observer component of the Abstract Architecture.
 */
interface WoDTDigitalTwinsObserver {

    /**
     * Flow to listen to new observed Digital Twins.
     * It emits the flow of the associated digital twin dtkgs.
     */
    val observedDigitalTwins: Flow<Pair<DigitalTwinURI, Flow<String>>>

    /** Start the observation of a newly registered WoDT Digital Twin. */
    suspend fun observeDigitalTwin(dtd: DigitalTwinDescription)

    /** Stop the observation of a deleted Digital Twin. */
    suspend fun stopObservationOfDigitalTwin(dtUri: DigitalTwinURI)
}
