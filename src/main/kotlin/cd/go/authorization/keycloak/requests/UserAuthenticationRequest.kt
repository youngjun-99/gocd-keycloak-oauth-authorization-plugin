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

import cd.go.authorization.keycloak.executors.UserAuthenticationRequestExecutor
import cd.go.authorization.keycloak.models.AuthConfig
import cd.go.authorization.keycloak.models.Role
import cd.go.authorization.keycloak.models.TokenInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest

class UserAuthenticationRequest : Request() {
    @Expose
    @SerializedName("auth_configs")
    private var authConfigs: List<AuthConfig>? = null

    @Expose
    @SerializedName("role_configs")
    private var roles: List<Role>? = null

    @Expose
    @SerializedName("credentials")
    private var tokenInfo: TokenInfo? = null

    fun authConfigs(): List<AuthConfig>? = authConfigs

    fun roles(): List<Role> = roles ?: emptyList()

    fun tokenInfo(): TokenInfo? = tokenInfo

    override fun executor(): UserAuthenticationRequestExecutor =
        UserAuthenticationRequestExecutor(this)

    companion object {
        @JvmStatic
        fun from(apiRequest: GoPluginApiRequest): UserAuthenticationRequest =
            Request.from(apiRequest, UserAuthenticationRequest::class.java)
    }
}
