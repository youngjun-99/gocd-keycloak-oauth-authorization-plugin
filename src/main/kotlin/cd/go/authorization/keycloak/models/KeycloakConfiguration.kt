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

import cd.go.authorization.keycloak.KeycloakApiClient
import cd.go.authorization.keycloak.annotation.ProfileField
import cd.go.authorization.keycloak.annotation.Validatable
import cd.go.authorization.keycloak.utils.Util.GSON
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

class KeycloakConfiguration : Validatable {

    @Expose
    @SerializedName("KeycloakEndpoint")
    @field:ProfileField(key = "KeycloakEndpoint", required = true, secure = false)
    private var keycloakEndpoint: String? = null

    @Expose
    @SerializedName("KeycloakRealm")
    @field:ProfileField(key = "KeycloakRealm", required = true, secure = false)
    private var keycloakRealm: String? = null

    @Expose
    @SerializedName("ClientId")
    @field:ProfileField(key = "ClientId", required = true, secure = false)
    private var clientId: String? = null

    @Expose
    @SerializedName("ClientSecret")
    @field:ProfileField(key = "ClientSecret", required = true, secure = true)
    private var clientSecret: String? = null

    @Transient
    private var keycloakApiClientInstance: KeycloakApiClient? = null

    constructor()

    constructor(keycloakEndpoint: String?, clientId: String?, clientSecret: String?) {
        this.keycloakEndpoint = keycloakEndpoint
        this.clientId = clientId
        this.clientSecret = clientSecret
    }

    fun keycloakEndpoint(): String? = keycloakEndpoint
    fun keycloakRealm(): String? = keycloakRealm
    fun clientId(): String? = clientId
    fun clientSecret(): String? = clientSecret

    fun toJSON(): String = GSON.toJson(this)

    override fun toProperties(): Map<String, String> {
        return GSON.fromJson(toJSON(), object : TypeToken<Map<String, String>>() {}.type)
    }

    fun keycloakApiClient(): KeycloakApiClient {
        if (keycloakApiClientInstance == null) {
            keycloakApiClientInstance = KeycloakApiClient(this)
        }
        return keycloakApiClientInstance!!
    }

    companion object {
        @JvmStatic
        fun fromJSON(json: String): KeycloakConfiguration = GSON.fromJson(json, KeycloakConfiguration::class.java)
    }
}
