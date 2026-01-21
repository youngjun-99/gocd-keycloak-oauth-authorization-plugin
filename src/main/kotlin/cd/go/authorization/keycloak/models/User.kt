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

import cd.go.authorization.keycloak.KeycloakUser
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    @Expose
    @SerializedName("username")
    private val username: String? = null,

    @Expose
    @SerializedName("display_name")
    private val displayName: String? = null,

    @Expose
    @SerializedName("email")
    private val emailId: String? = null
) {
    constructor(userProfile: KeycloakUser) : this(
        userProfile.email,
        userProfile.name,
        userProfile.email?.lowercase()?.trim()
    )

    fun username(): String? = username
    fun displayName(): String? = displayName
    fun emailId(): String? = emailId
}
