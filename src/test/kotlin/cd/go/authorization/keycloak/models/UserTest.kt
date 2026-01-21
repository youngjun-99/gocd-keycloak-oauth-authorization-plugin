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
import cd.go.authorization.keycloak.utils.Util.GSON
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class UserTest {

    @Test
    fun shouldSerializeToJSON() {
        val user = User("foo", "bar", "baz")
        val expectedJSON = """{"username":"foo","display_name":"bar","email":"baz"}"""

        JSONAssert.assertEquals(expectedJSON, GSON.toJson(user), true)
    }

    @Test
    fun shouldCreateUserFromKeycloakUser() {
        val keycloakUser = KeycloakUser("foo@bar.com", "Foo Bar")
        val user = User(keycloakUser)

        assertThat(user.username(), `is`("foo@bar.com"))
        assertThat(user.displayName(), `is`("Foo Bar"))
        assertThat(user.emailId(), `is`("foo@bar.com"))
    }
}
