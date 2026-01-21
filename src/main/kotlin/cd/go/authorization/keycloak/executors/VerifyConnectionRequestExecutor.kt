/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package cd.go.authorization.keycloak.executors

import cd.go.authorization.keycloak.KeycloakApiClient
import cd.go.authorization.keycloak.annotation.MetadataValidator
import cd.go.authorization.keycloak.annotation.ValidationResult
import cd.go.authorization.keycloak.requests.VerifyConnectionRequest
import cd.go.authorization.keycloak.utils.Util.GSON
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse

class VerifyConnectionRequestExecutor(
    private val request: VerifyConnectionRequest,
    private val providerManager: KeycloakApiClient
) : RequestExecutor {

    constructor(request: VerifyConnectionRequest) : this(request, KeycloakApiClient(request.keycloakConfiguration()))

    override fun execute(): GoPluginApiResponse {
        val validationResult = validate()
        if (validationResult.hasErrors()) {
            return validationFailureResponse(validationResult)
        }

        return successResponse()
    }

    private fun validate(): ValidationResult =
        MetadataValidator().validate(request.keycloakConfiguration())

    private fun successResponse(): GoPluginApiResponse =
        responseWith("success", "Connection ok", null)

    private fun validationFailureResponse(errors: ValidationResult): GoPluginApiResponse =
        responseWith("validation-failed", "Validation failed for the given Auth Config", errors)

    private fun responseWith(status: String, message: String, result: ValidationResult?): GoPluginApiResponse {
        val response = mutableMapOf<String, Any>(
            "status" to status,
            "message" to message
        )

        if (result != null && result.hasErrors()) {
            response["errors"] = result.errors()
        }

        return DefaultGoPluginApiResponse.success(GSON.toJson(response))
    }
}
