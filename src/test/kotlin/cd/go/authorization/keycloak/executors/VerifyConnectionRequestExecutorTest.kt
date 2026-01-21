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

package cd.go.authorization.keycloak.executors

import cd.go.authorization.keycloak.KeycloakApiClient
import cd.go.authorization.keycloak.models.KeycloakConfiguration
import cd.go.authorization.keycloak.requests.VerifyConnectionRequest
import com.google.gson.JsonParser
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VerifyConnectionRequestExecutorTest {

    @Mock
    private lateinit var request: VerifyConnectionRequest
    @Mock
    private lateinit var keycloakApiClient: KeycloakApiClient

    @Test
    fun shouldReturnFailureResponseIfVerifyConnectionFails() {
        val emptyConfig = KeycloakConfiguration.fromJSON("{}")
        `when`(request.keycloakConfiguration()).thenReturn(emptyConfig)

        val executor = VerifyConnectionRequestExecutor(request, keycloakApiClient)
        val response = executor.execute()

        assertThat(response.responseCode(), `is`(200))

        val json = JsonParser.parseString(response.responseBody()).asJsonObject
        assertThat(json.get("status").asString, `is`("validation-failed"))
        assertThat(json.get("message").asString, `is`("Validation failed for the given Auth Config"))
        assertThat(json.has("errors"), `is`(true))
    }

    @Test
    fun shouldReturnSuccessResponseOnSuccessfulVerification() {
        val validConfig = KeycloakConfiguration.fromJSON("""
            {
                "KeycloakEndpoint": "https://keycloak.example.com",
                "KeycloakRealm": "master",
                "ClientId": "client-id",
                "ClientSecret": "client-secret"
            }
        """.trimIndent())
        `when`(request.keycloakConfiguration()).thenReturn(validConfig)

        val executor = VerifyConnectionRequestExecutor(request, keycloakApiClient)
        val response = executor.execute()

        assertThat(response.responseCode(), `is`(200))

        val json = JsonParser.parseString(response.responseBody()).asJsonObject
        assertThat(json.get("status").asString, `is`("success"))
        assertThat(json.get("message").asString, `is`("Connection ok"))
    }
}
