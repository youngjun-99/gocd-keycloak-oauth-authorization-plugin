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

package cd.go.authorization.keycloak.annotation

import cd.go.authorization.keycloak.utils.Util.isBlank
import cd.go.authorization.keycloak.utils.Util.isNotBlank
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileMetadata<T : Metadata>(
    @Expose
    @SerializedName("key")
    private val key: String,

    @Expose
    @SerializedName("metadata")
    private val metadata: T
) {
    fun validate(input: String?): ValidationError? {
        val validationError = doValidate(input)
        return if (isNotBlank(validationError)) {
            ValidationError(key, validationError!!)
        } else null
    }

    protected fun doValidate(input: String?): String? {
        if (isRequired) {
            if (isBlank(input)) {
                return "$key must not be blank."
            }
        }
        return null
    }

    fun getKey(): String = key

    val isRequired: Boolean get() = metadata.isRequired
}
