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

import cd.go.authorization.keycloak.exceptions.NoAuthorizationConfigurationException
import cd.go.authorization.keycloak.requests.GetAuthorizationServerUrlRequest
import cd.go.authorization.keycloak.utils.Util.GSON
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse

class GetAuthorizationServerUrlRequestExecutor(
    private val request: GetAuthorizationServerUrlRequest
) : RequestExecutor {

    override fun execute(): GoPluginApiResponse {
        if (request.authConfigs().isEmpty()) {
            throw NoAuthorizationConfigurationException("[Authorization Server Url] No authorization configuration found.")
        }

        val keycloakApiClient = request.authConfigs()[0].configuration!!.keycloakApiClient()

        return DefaultGoPluginApiResponse.success(
            GSON.toJson(mapOf("authorization_server_url" to keycloakApiClient.authorizationServerUrl(request.callbackUrl() ?: "")))
        )
    }
}
