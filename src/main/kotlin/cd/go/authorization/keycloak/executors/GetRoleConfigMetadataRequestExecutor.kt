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

import cd.go.authorization.keycloak.annotation.MetadataHelper
import cd.go.authorization.keycloak.models.KeycloakRoleConfiguration
import com.google.gson.GsonBuilder
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse

class GetRoleConfigMetadataRequestExecutor : RequestExecutor {
    companion object {
        private val GSON = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    override fun execute(): GoPluginApiResponse {
        val authConfigMetadata = MetadataHelper.getMetadata(KeycloakRoleConfiguration::class.java)
        return DefaultGoPluginApiResponse.success(GSON.toJson(authConfigMetadata))
    }
}
