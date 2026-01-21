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

import cd.go.authorization.keycloak.KeycloakAuthorizer
import cd.go.authorization.keycloak.KeycloakPlugin
import cd.go.authorization.keycloak.exceptions.NoAuthorizationConfigurationException
import cd.go.authorization.keycloak.models.User
import cd.go.authorization.keycloak.requests.UserAuthenticationRequest
import com.google.gson.Gson
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse

class UserAuthenticationRequestExecutor(
    private val request: UserAuthenticationRequest,
    private val keycloakAuthorizer: KeycloakAuthorizer
) : RequestExecutor {

    constructor(request: UserAuthenticationRequest) : this(request, KeycloakAuthorizer())

    companion object {
        private val GSON = Gson()
    }

    override fun execute(): GoPluginApiResponse {
        if (request.authConfigs().isNullOrEmpty()) {
            throw NoAuthorizationConfigurationException("[Authenticate] No authorization configuration found.")
        }

        try {
            val authConfig = request.authConfigs()!![0]
            val configuration = authConfig.configuration!!
            val keycloakApiClient = configuration.keycloakApiClient()

            KeycloakPlugin.LOG.info("[UserAuthenticationRequestExecutor] Fetching user profile from Keycloak...")
            val keycloakUser = keycloakApiClient.userProfile(request.tokenInfo()!!)
            KeycloakPlugin.LOG.info("[UserAuthenticationRequestExecutor] User profile fetched successfully: ${keycloakUser.preferredUsername}")

            val userMap = mapOf(
                "user" to User(keycloakUser),
                "roles" to keycloakAuthorizer.authorize(keycloakUser, request.roles())
            )

            return DefaultGoPluginApiResponse.success(GSON.toJson(userMap))
        } catch (e: Exception) {
            KeycloakPlugin.LOG.error("[UserAuthenticationRequestExecutor] Authentication failed: ${e.message}", e)
            throw e
        }
    }
}
