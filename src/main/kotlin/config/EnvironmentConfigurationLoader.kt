/*
 * Copyright (c) 2024. Andrea Giulianelli
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

package config

import java.net.URI

/**
 * This class models a configuration loader that use Environmental variables.
 */
class EnvironmentConfigurationLoader : ConfigurationLoader {
    init {
        checkNotNull(System.getenv(EXPOSED_PORT_VARIABLE)) { "Please provide the exposed port" }
        checkNotNull(System.getenv(PLATFORM_EXPOSED_URL_VARIABLE)) { "Please provide the WoDT Platform exposed URL" }
    }

    override val exposedPort: Int = System.getenv(EXPOSED_PORT_VARIABLE).toInt()
    override val platformExposedUrl: URI = URI.create(System.getenv(PLATFORM_EXPOSED_URL_VARIABLE))

    companion object {
        private const val EXPOSED_PORT_VARIABLE = "EXPOSED_PORT"
        private const val PLATFORM_EXPOSED_URL_VARIABLE = "PLATFORM_EXPOSED_URL"
    }
}
