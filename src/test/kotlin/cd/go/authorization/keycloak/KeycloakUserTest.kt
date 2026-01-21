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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class KeycloakUserTest {

    @Test
    fun shouldDeserializeJSON() {
        val keycloakUser = KeycloakUser.fromJSON("""
            {
             "email": "foo@example.com",
             "email_verified": true,
             "name": "Foo Bar",
             "given_name": "Bar",
             "family_name": "Foo",
             "locale": "en",
             "preferred_username": "foo@example.com",
             "sub": "00uea8uu",
             "updated_at": 1520793947,
             "zoneinfo": "America/Los_Angeles"
            }
        """.trimIndent())

        assertThat(keycloakUser.email, `is`("foo@example.com"))
        assertThat(keycloakUser.isVerifiedEmail, `is`(true))
        assertThat(keycloakUser.name, `is`("Foo Bar"))
        assertThat(keycloakUser.givenName, `is`("Bar"))
        assertThat(keycloakUser.familyName, `is`("Foo"))
        assertThat(keycloakUser.locale, `is`("en"))
        assertThat(keycloakUser.preferredUsername, `is`("foo@example.com"))
        assertThat(keycloakUser.sub, `is`("00uea8uu"))
        assertThat(keycloakUser.updatedAt, `is`(1520793947))
        assertThat(keycloakUser.zoneInfo, `is`("America/Los_Angeles"))
    }
}
