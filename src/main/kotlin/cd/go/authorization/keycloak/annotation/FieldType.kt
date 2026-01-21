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

enum class FieldType {
    STRING {
        override fun validate(value: String?): String? = null
    },
    POSITIVE_DECIMAL {
        override fun validate(value: String?): String? {
            return try {
                if (value!!.toLong() < 0) "must be positive decimal" else null
            } catch (e: Exception) {
                "must be positive decimal"
            }
        }
    },
    NUMBER {
        override fun validate(value: String?): String? {
            return try {
                if (value!!.toDouble() < 0) "must be number" else null
            } catch (e: Exception) {
                "must be number"
            }
        }
    };

    abstract fun validate(value: String?): String?
}
