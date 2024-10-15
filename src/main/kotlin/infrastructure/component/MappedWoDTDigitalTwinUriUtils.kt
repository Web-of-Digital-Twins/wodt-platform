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

package infrastructure.component

/**
 * Obtain the WoDT Digital Twin Uri from the path parameters inside the request url.
 */
fun obtainDigitalTwinUriFromPathParameters(pathParameters: List<String>): String {
    var path = pathParameters[0] + "//" + pathParameters[1]
    if (pathParameters.size > 2) {
        path += pathParameters.subList(2, pathParameters.size).joinToString(
            prefix = "/", separator = "/",
        )
    }
    return path
}
