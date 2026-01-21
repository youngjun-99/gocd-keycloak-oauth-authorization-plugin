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
import cd.go.authorization.keycloak.utils.Util.GSON
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse

abstract class Request {
    @Transient
    protected var apiRequest: GoPluginApiRequest? = null

    abstract fun executor(): RequestExecutor

    fun requestParameters(): Map<String, String> = apiRequest?.requestParameters() ?: emptyMap()

    fun requestHeaders(): Map<String, String> = apiRequest?.requestHeaders() ?: emptyMap()

    @Throws(Exception::class)
    fun execute(): GoPluginApiResponse = executor().execute()

    companion object {
        @JvmStatic
        fun <T : Request> from(apiRequest: GoPluginApiRequest, clazz: Class<T>): T {
            val request = GSON.fromJson(apiRequest.requestBody(), clazz)
            request.apiRequest = apiRequest
            return request
        }
    }
}
