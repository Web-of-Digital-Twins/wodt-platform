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

package entity.digitaltwin

/**
 * This interface represents the abstraction of Digital Twin Description of a WoDT Digital Twin.
 */
interface DigitalTwinDescription {
    /** The physical asset identifier of the associated physical asset. */
    val physicalAssetId: String

    /** The URI of the interested WoDT Digital Twin. */
    val digitalTwinUri: DigitalTwinURI

    /** The type of representation used to implement the provided Digital Twin Description. */
    val implementationType: DigitalTwinDescriptionImplementationType

    /** Obtain the form to observe the WoDT Digital Twin. */
    fun obtainObservationForm(): Form

    /**
     * Obtain the string representation of the Digital Twin Description.
     * The type of it will be the one indicated in [implementationType].
     */
    fun obtainRepresentation(): String
}

/**
 * This class represents a form in the [DigitalTwinDescription].
 * Each form is described by its [href] and its [protocol].
 */
data class Form(val href: String, val protocol: FormProtocol)

/**
 * This enum lists the supported protocol within the form of a WoDT Digital Twin.
 * Each protocol has a [protocolName].
 */
enum class FormProtocol(val protocolName: String) {
    WEBSOCKET("websocket"),
}

/**
 * This enum lists the supported implementation of the Digital Twin Description.
 * Each implementation is associated to its [contentType].
 */
enum class DigitalTwinDescriptionImplementationType(val contentType: String) {
    THING_DESCRIPTION("application/td+json"),
}
