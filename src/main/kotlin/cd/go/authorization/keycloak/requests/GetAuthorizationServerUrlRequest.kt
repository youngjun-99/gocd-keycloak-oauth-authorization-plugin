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

import cd.go.authorization.keycloak.CallbackURL
import cd.go.authorization.keycloak.executors.GetAuthorizationServerUrlRequestExecutor
import cd.go.authorization.keycloak.models.AuthConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest

class GetAuthorizationServerUrlRequest : Request() {
    @Expose
    @SerializedName("authorization_server_callback_url")
    private var callbackUrl: String? = null

    @Expose
    @SerializedName("auth_configs")
    private var authConfigs: List<AuthConfig>? = null

    fun callbackUrl(): String? = callbackUrl

    fun authConfigs(): List<AuthConfig> = authConfigs ?: emptyList()

    override fun executor(): GetAuthorizationServerUrlRequestExecutor =
        GetAuthorizationServerUrlRequestExecutor(this)

    companion object {
        @JvmStatic
        fun from(apiRequest: GoPluginApiRequest): GetAuthorizationServerUrlRequest {
            val request = Request.from(apiRequest, GetAuthorizationServerUrlRequest::class.java)
            CallbackURL.instance().updateRedirectURL(request.callbackUrl)
            return request
        }
    }
}
