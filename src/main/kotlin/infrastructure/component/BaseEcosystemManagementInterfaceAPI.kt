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

import application.component.EcosystemManagementInterface
import application.presenter.dtd.DigitalTwinDescriptionDeserialization.toDTD
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

/**
 * API exposed by the Ecosystem Management Interface.
 * Requests are handled by the [ecosystemManagementInterface].
 */
fun Application.ecosystemManagementAPI(ecosystemManagementInterface: EcosystemManagementInterface) {
    routing {
        registerNewDigitalTwin(ecosystemManagementInterface)
        deleteDigitalTwin(ecosystemManagementInterface)
    }
}

private fun Route.registerNewDigitalTwin(ecosystemManagementInterface: EcosystemManagementInterface) {
    post("/wodt") {
        val dtdText = call.receiveText()
        val contentType = call.request.contentType().toString()
        val shouldNotify = call.request.queryParameters["admin"]?.toBoolean() == true

        if (dtdText.toDTD(contentType) == null) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            call.respond(
                if (ecosystemManagementInterface.registerNewDigitalTwin(dtdText, contentType, shouldNotify)) {
                    HttpStatusCode.Accepted
                } else {
                    HttpStatusCode.Conflict
                },
            )
        }
    }
}

private fun Route.deleteDigitalTwin(ecosystemManagementInterface: EcosystemManagementInterface) {
    delete("/wodt/{dtUri...}") {
        call.parameters.getAll("dtUri")?.also { pathParameters ->
            if (pathParameters.size >= 2) {
                val dtUri = obtainDigitalTwinUriFromPathParameters(pathParameters)
                call.respond(
                    if (ecosystemManagementInterface.deleteDigitalTwin(dtUri)) {
                        HttpStatusCode.Accepted
                    } else {
                        HttpStatusCode.NotFound
                    },
                )
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
