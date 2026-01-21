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

package cd.go.authorization.keycloak.models

import cd.go.authorization.keycloak.utils.Util.GSON
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Role(
    @Expose
    @SerializedName("name")
    private val name: String? = null,

    @Expose
    @SerializedName("auth_config_id")
    private val authConfigId: String? = null,

    @Expose
    @SerializedName("configuration")
    private val configuration: KeycloakRoleConfiguration? = null
) {
    fun name(): String? = name
    fun roleConfiguration(): KeycloakRoleConfiguration? = configuration
    fun authConfigId(): String? = authConfigId

    companion object {
        @JvmStatic
        fun fromJSON(json: String): Role = GSON.fromJson(json, Role::class.java)
    }
}
