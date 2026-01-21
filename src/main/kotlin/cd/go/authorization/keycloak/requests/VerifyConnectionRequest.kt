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

package cd.go.authorization.keycloak.requests

import cd.go.authorization.keycloak.executors.RequestExecutor
import cd.go.authorization.keycloak.executors.VerifyConnectionRequestExecutor
import cd.go.authorization.keycloak.models.KeycloakConfiguration
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest

class VerifyConnectionRequest private constructor(
    private val configuration: KeycloakConfiguration
) : Request() {

    fun keycloakConfiguration(): KeycloakConfiguration = configuration

    override fun executor(): RequestExecutor = VerifyConnectionRequestExecutor(this)

    companion object {
        @JvmStatic
        fun from(apiRequest: GoPluginApiRequest): VerifyConnectionRequest =
            VerifyConnectionRequest(KeycloakConfiguration.fromJSON(apiRequest.requestBody()))
    }
}
