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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KeycloakRoleConfigurationTest {

    @Test
    fun shouldDeserializeFromJSON() {
        val config = KeycloakRoleConfiguration.fromJSON("""
            {
              "Groups": "admins, developers",
              "Users": "user1@test.com, user2@test.com"
            }
        """.trimIndent())

        assertThat(config.groups(), contains("admins", "developers"))
        assertThat(config.users(), contains("user1@test.com", "user2@test.com"))
    }

    @Test
    fun shouldReturnEmptyListsForNullValues() {
        val config = KeycloakRoleConfiguration.fromJSON("{}")

        assertThat(config.groups(), `is`(empty()))
        assertThat(config.users(), `is`(empty()))
    }

    @Test
    fun shouldLowercaseUserEmails() {
        val config = KeycloakRoleConfiguration.fromJSON("""{"Users": "Foo@Bar.com, ADMIN@TEST.COM"}""")

        assertThat(config.users(), contains("foo@bar.com", "admin@test.com"))
    }

    @Test
    fun shouldHaveConfigurationWhenGroupsPresent() {
        val config = KeycloakRoleConfiguration.fromJSON("""{"Groups": "admins"}""")
        assertTrue(config.hasConfiguration())
    }

    @Test
    fun shouldHaveConfigurationWhenUsersPresent() {
        val config = KeycloakRoleConfiguration.fromJSON("""{"Users": "user@test.com"}""")
        assertTrue(config.hasConfiguration())
    }

    @Test
    fun shouldNotHaveConfigurationWhenEmpty() {
        val config = KeycloakRoleConfiguration.fromJSON("{}")
        assertFalse(config.hasConfiguration())
    }

    @Test
    fun shouldConvertToProperties() {
        val config = KeycloakRoleConfiguration("admins", "user@test.com")

        val properties = config.toProperties()

        assertThat(properties, hasEntry("Groups", "admins"))
        assertThat(properties, hasEntry("Users", "user@test.com"))
    }
}
