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

/**
 * This interface models the Http client that send request for the various components of the Abstract Architecture.
 */
interface WoDTPlatformHttpClient {
    /**
     * Http request to notify the registration of a WoDT Digital Twin, identified by its [dtUri], by an administrator.
     */
    suspend fun sendRegistrationNotification(dtUri: String): Boolean
}
