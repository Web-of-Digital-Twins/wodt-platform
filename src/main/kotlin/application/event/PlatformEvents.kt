/*
 * Copyright (c) 2025. Andrea Giulianelli
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

package application.event

import entity.digitaltwin.DigitalTwinURI

/**
 * Interface of events within the Platform.
 * It tracks the [dtUri] of the dt that caused the event along with the [dtMessageCounter].
 */
sealed interface PlatformEvent {
    val dtMessageCounter: Int?
    val dtUri: DigitalTwinURI
}

/**
 * Class that models the event of receiving raw dtkg string data from a DT.
 * Each event carries its [dtMessageCounter], [dtUri], and its dtkg [dtkgPayload] as a string.
 */
data class DtkgEvent(
    override val dtMessageCounter: Int,
    override val dtUri: DigitalTwinURI,
    val dtkgPayload: String,
) : PlatformEvent

/**
 * Class that models the event of DT ecosystem KG update internal to the Platform.
 * Each event carries the [dtUri] and the [dtMessageCounter] of the underlying DT that originates the update and
 * the [dtEcosystemKGPayload].
 * N.B. Each update is the result of an update of a single DT, that is the one reported in the event itself in the
 * [dtUri] and [dtMessageCounter] fields.
 */
data class DTEcosystemKGEvent(
    override val dtMessageCounter: Int?,
    override val dtUri: DigitalTwinURI,
    val dtEcosystemKGPayload: String,
) : PlatformEvent
