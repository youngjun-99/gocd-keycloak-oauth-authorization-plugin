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

package cd.go.authorization.keycloak

import cd.go.authorization.keycloak.exceptions.NoSuchRequestHandlerException
import cd.go.authorization.keycloak.executors.*
import cd.go.authorization.keycloak.requests.*
import com.thoughtworks.go.plugin.api.GoApplicationAccessor
import com.thoughtworks.go.plugin.api.GoPlugin
import com.thoughtworks.go.plugin.api.GoPluginIdentifier
import com.thoughtworks.go.plugin.api.annotation.Extension
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException
import com.thoughtworks.go.plugin.api.logging.Logger
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse

@Extension
class KeycloakPlugin : GoPlugin {
    private var accessor: GoApplicationAccessor? = null

    override fun initializeGoApplicationAccessor(accessor: GoApplicationAccessor) {
        this.accessor = accessor
    }

    override fun handle(request: GoPluginApiRequest): GoPluginApiResponse? {
        try {
            return when (RequestFromServer.fromString(request.requestName())) {
                RequestFromServer.REQUEST_GET_PLUGIN_ICON -> GetPluginIconRequestExecutor().execute()
                RequestFromServer.REQUEST_GET_CAPABILITIES -> GetCapabilitiesRequestExecutor().execute()
                RequestFromServer.REQUEST_GET_AUTH_CONFIG_METADATA -> GetAuthConfigMetadataRequestExecutor().execute()
                RequestFromServer.REQUEST_AUTH_CONFIG_VIEW -> GetAuthConfigViewRequestExecutor().execute()
                RequestFromServer.REQUEST_VALIDATE_AUTH_CONFIG -> AuthConfigValidateRequest.from(request).execute()
                RequestFromServer.REQUEST_VERIFY_CONNECTION -> VerifyConnectionRequest.from(request).execute()
                RequestFromServer.REQUEST_GET_ROLE_CONFIG_METADATA -> GetRoleConfigMetadataRequestExecutor().execute()
                RequestFromServer.REQUEST_ROLE_CONFIG_VIEW -> GetRoleConfigViewRequestExecutor().execute()
                RequestFromServer.REQUEST_VALIDATE_ROLE_CONFIG -> RoleConfigValidateRequest.from(request).execute()
                RequestFromServer.REQUEST_AUTHORIZATION_SERVER_REDIRECT_URL -> GetAuthorizationServerUrlRequest.from(request).execute()
                RequestFromServer.REQUEST_ACCESS_TOKEN -> FetchAccessTokenRequest.from(request).execute()
                RequestFromServer.REQUEST_AUTHENTICATE_USER -> UserAuthenticationRequest.from(request).execute()
                else -> throw UnhandledRequestTypeException(request.requestName())
            }
        } catch (e: NoSuchRequestHandlerException) {
            LOG.warn(e.message)
            return null
        } catch (e: Exception) {
            LOG.error("Error while executing request ${request.requestName()}", e)
            throw RuntimeException(e)
        }
    }

    override fun pluginIdentifier(): GoPluginIdentifier = Constants.PLUGIN_IDENTIFIER

    companion object {
        @JvmField
        val LOG: Logger = Logger.getLoggerFor(KeycloakPlugin::class.java)
    }
}
