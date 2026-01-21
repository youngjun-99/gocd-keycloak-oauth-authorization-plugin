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

import cd.go.authorization.keycloak.annotation.ProfileField
import cd.go.authorization.keycloak.annotation.Validatable
import cd.go.authorization.keycloak.utils.Util.GSON
import cd.go.authorization.keycloak.utils.Util.isNotBlank
import cd.go.authorization.keycloak.utils.Util.listFromCommaSeparatedString
import cd.go.authorization.keycloak.utils.Util.toLowerCase
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

class KeycloakRoleConfiguration(
    @Expose
    @SerializedName("Groups")
    @field:ProfileField(key = "Groups", required = false, secure = false)
    private var groups: String? = null,

    @Expose
    @SerializedName("Users")
    @field:ProfileField(key = "Users", required = false, secure = false)
    private var users: String? = null
) : Validatable {

    constructor() : this(null, null)

    fun groups(): List<String> = listFromCommaSeparatedString(groups)
    fun users(): List<String> = listFromCommaSeparatedString(toLowerCase(users))

    fun toJSON(): String = GSON.toJson(this)

    override fun toProperties(): Map<String, String> {
        return GSON.fromJson(toJSON(), object : TypeToken<Map<String, String>>() {}.type)
    }

    fun hasConfiguration(): Boolean = isNotBlank(groups) || isNotBlank(users)

    companion object {
        @JvmStatic
        fun fromJSON(json: String): KeycloakRoleConfiguration = GSON.fromJson(json, KeycloakRoleConfiguration::class.java)
    }
}
