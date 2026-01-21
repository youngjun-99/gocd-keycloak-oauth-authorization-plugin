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

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasEntry
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class KeycloakConfigurationTest {

    @Test
    fun shouldDeserializeKeycloakConfiguration() {
        val keycloakConfiguration = KeycloakConfiguration.fromJSON("""
            {
              "KeycloakEndpoint": "https://example.co.in",
              "ClientId": "client-id",
              "ClientSecret": "client-secret"
            }
        """.trimIndent())

        assertThat(keycloakConfiguration.keycloakEndpoint(), `is`("https://example.co.in"))
        assertThat(keycloakConfiguration.clientId(), `is`("client-id"))
        assertThat(keycloakConfiguration.clientSecret(), `is`("client-secret"))
    }

    @Test
    fun shouldSerializeToJSON() {
        val keycloakConfiguration = KeycloakConfiguration(
            "https://example.co.in", "client-id", "client-secret"
        )

        val expectedJSON = """
            {
              "KeycloakEndpoint": "https://example.co.in",
              "ClientId": "client-id",
              "ClientSecret": "client-secret"
            }
        """.trimIndent()

        JSONAssert.assertEquals(expectedJSON, keycloakConfiguration.toJSON(), true)
    }

    @Test
    fun shouldConvertConfigurationToProperties() {
        val keycloakConfiguration = KeycloakConfiguration(
            "https://example.co.in", "client-id", "client-secret"
        )

        val properties = keycloakConfiguration.toProperties()

        assertThat(properties, hasEntry("KeycloakEndpoint", "https://example.co.in"))
        assertThat(properties, hasEntry("ClientId", "client-id"))
        assertThat(properties, hasEntry("ClientSecret", "client-secret"))
    }
}
