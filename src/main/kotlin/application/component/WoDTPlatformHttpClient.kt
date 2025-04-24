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

import application.event.DtkgEvent

/**
 * This interface models the Http client that send request for the [EcosystemManagementInterface] component
 * of the Abstract Architecture.
 */
interface EcosystemManagementHttpClient {
    /**
     * Http request to notify the registration of a WoDT Digital Twin, identified by its [dtUri], by an administrator.
     */
    suspend fun sendRegistrationNotification(dtUri: String): Boolean
}

/**
 * This interface models the Web Socket client that observe the registered WoDT Digital Twins
 * for the [WoDTDigitalTwinsObserver] component of the Abstract Architecture.
 */
interface WoDTDigitalTwinsObserverWsClient {
    /**
     * Observe a Digital Twin using a websocket at a [wsDtUri] and handling new data with [onData] and
     * the close of the websocket with [onClose] lambdas.
     */
    suspend fun observeDigitalTwin(wsDtUri: String, onData: suspend (DtkgEvent) -> Unit, onClose: suspend () -> Unit)

    /** Stop observation of the Digital Twin at its [dtUri]. */
    suspend fun stopObservationOfDigitalTwin(dtUri: String)
}
