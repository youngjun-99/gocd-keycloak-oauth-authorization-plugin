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

import cd.go.authorization.keycloak.utils.Util.GSON
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class KeycloakUser {
    @Expose
    @SerializedName("email")
    var email: String? = null
        private set

    @Expose
    @SerializedName("email_verified")
    var isVerifiedEmail: Boolean = false
        private set

    @Expose
    @SerializedName("name")
    var name: String? = null
        private set

    @Expose
    @SerializedName("given_name")
    var givenName: String? = null
        private set

    @Expose
    @SerializedName("family_name")
    var familyName: String? = null
        private set

    @Expose
    @SerializedName("locale")
    var locale: String? = null
        private set

    @Expose
    @SerializedName("preferred_username")
    var preferredUsername: String? = null
        private set

    @Expose
    @SerializedName("sub")
    var sub: String? = null
        private set

    @Expose
    @SerializedName("updated_at")
    var updatedAt: Int = 0
        private set

    @Expose
    @SerializedName("zoneinfo")
    var zoneInfo: String? = null
        private set

    @Expose
    @SerializedName("groups")
    private var groups: List<String>? = null

    constructor()

    constructor(email: String?, name: String?) {
        this.email = email
        this.name = name
    }

    fun groups(): List<String>? = groups

    fun toJSON(): String = GSON.toJson(this)

    companion object {
        @JvmStatic
        fun fromJSON(json: String): KeycloakUser = GSON.fromJson(json, KeycloakUser::class.java)
    }
}
