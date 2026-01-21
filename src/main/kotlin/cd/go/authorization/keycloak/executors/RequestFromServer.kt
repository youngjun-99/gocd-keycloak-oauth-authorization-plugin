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

import cd.go.authorization.keycloak.exceptions.NoSuchRequestHandlerException

enum class RequestFromServer(val requestName: String) {
    REQUEST_GET_PLUGIN_ICON("${Constants.REQUEST_PREFIX}.get-icon"),
    REQUEST_GET_CAPABILITIES("${Constants.REQUEST_PREFIX}.get-capabilities"),

    REQUEST_GET_AUTH_CONFIG_METADATA("${Constants.REQUEST_PREFIX}.${Constants.AUTH_CONFIG_METADATA}.get-metadata"),
    REQUEST_AUTH_CONFIG_VIEW("${Constants.REQUEST_PREFIX}.${Constants.AUTH_CONFIG_METADATA}.get-view"),
    REQUEST_VALIDATE_AUTH_CONFIG("${Constants.REQUEST_PREFIX}.${Constants.AUTH_CONFIG_METADATA}.validate"),
    REQUEST_VERIFY_CONNECTION("${Constants.REQUEST_PREFIX}.${Constants.AUTH_CONFIG_METADATA}.verify-connection"),

    REQUEST_GET_ROLE_CONFIG_METADATA("${Constants.REQUEST_PREFIX}.${Constants.ROLE_CONFIG_METADATA}.get-metadata"),
    REQUEST_ROLE_CONFIG_VIEW("${Constants.REQUEST_PREFIX}.${Constants.ROLE_CONFIG_METADATA}.get-view"),
    REQUEST_VALIDATE_ROLE_CONFIG("${Constants.REQUEST_PREFIX}.${Constants.ROLE_CONFIG_METADATA}.validate"),

    REQUEST_AUTHENTICATE_USER("${Constants.REQUEST_PREFIX}.authenticate-user"),
    REQUEST_SEARCH_USERS("${Constants.REQUEST_PREFIX}.search-users"),

    REQUEST_AUTHORIZATION_SERVER_REDIRECT_URL("${Constants.REQUEST_PREFIX}.authorization-server-url"),
    REQUEST_ACCESS_TOKEN("${Constants.REQUEST_PREFIX}.fetch-access-token");

    private object Constants {
        const val REQUEST_PREFIX = "go.cd.authorization"
        const val AUTH_CONFIG_METADATA = "auth-config"
        const val ROLE_CONFIG_METADATA = "role-config"
    }

    companion object {
        @JvmStatic
        fun fromString(requestName: String?): RequestFromServer {
            if (requestName != null) {
                for (requestFromServer in entries) {
                    if (requestName.equals(requestFromServer.requestName, ignoreCase = true)) {
                        return requestFromServer
                    }
                }
            }
            throw NoSuchRequestHandlerException("Request $requestName is not supported by plugin.")
        }
    }
}
