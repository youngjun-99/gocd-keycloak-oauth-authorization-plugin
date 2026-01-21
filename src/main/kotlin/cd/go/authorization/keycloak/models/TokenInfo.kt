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

data class TokenInfo(
    @Expose
    @SerializedName("access_token")
    private val accessToken: String? = null,

    @Expose
    @SerializedName("expires_in")
    private val expiresIn: Long = 0,

    @Expose
    @SerializedName("token_type")
    private val tokenType: String? = null,

    @Expose
    @SerializedName("refresh_token")
    private val refreshToken: String? = null
) {
    fun accessToken(): String? = accessToken
    fun tokenType(): String? = tokenType
    fun expiresIn(): Long = expiresIn
    fun refreshToken(): String? = refreshToken

    fun toJSON(): String = GSON.toJson(this)

    companion object {
        @JvmStatic
        fun fromJSON(json: String): TokenInfo = GSON.fromJson(json, TokenInfo::class.java)
    }
}
