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

import cd.go.authorization.keycloak.utils.Util.GSON

class ValidationResult {
    private val errors: MutableSet<ValidationError> = HashSet()

    constructor()

    constructor(errors: Collection<ValidationError>) {
        this.errors.addAll(errors)
    }

    fun addError(key: String, message: String) {
        errors.add(ValidationError(key, message))
    }

    fun addError(validationError: ValidationError?) {
        validationError?.let { errors.add(it) }
    }

    fun hasErrors(): Boolean = errors.isNotEmpty()

    fun toJSON(): String = GSON.toJson(errors)

    fun hasKey(key: String): Boolean = errors.any { key == it.key() }

    fun errors(): List<ValidationError> = errors.toList()
}
