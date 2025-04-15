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

package entity.event

import entity.digitaltwin.DigitalTwinDescription
import entity.digitaltwin.DigitalTwinURI

/** Interface that models the ecosystem events within the WoDT Platform.  */
sealed interface EcosystemEvent

/** This class models the event of a new WoDT Digital Twin registered to the Platform
 * via its [dtd] and the identifying URI [dtURI]. */
data class NewDigitalTwinRegistered(
    val dtd: DigitalTwinDescription,
    val dtURI: DigitalTwinURI = dtd.digitalTwinUri,
) : EcosystemEvent

/** This class models the event of deletion of a registered WoDT Digital Twin, identified by its [dtURI]. */
data class DigitalTwinDeleted(val dtURI: DigitalTwinURI) : EcosystemEvent
