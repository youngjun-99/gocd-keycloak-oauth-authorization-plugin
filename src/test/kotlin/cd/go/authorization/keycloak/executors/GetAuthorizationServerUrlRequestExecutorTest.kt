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
import cd.go.authorization.keycloak.exceptions.NoAuthorizationConfigurationException
import cd.go.authorization.keycloak.models.AuthConfig
import cd.go.authorization.keycloak.models.KeycloakConfiguration
import cd.go.authorization.keycloak.requests.GetAuthorizationServerUrlRequest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetAuthorizationServerUrlRequestExecutorTest {

    @Mock
    private lateinit var request: GetAuthorizationServerUrlRequest
    @Mock
    private lateinit var authConfig: AuthConfig
    @Mock
    private lateinit var keycloakConfiguration: KeycloakConfiguration
    @Mock
    private lateinit var keycloakApiClient: KeycloakApiClient

    private lateinit var executor: GetAuthorizationServerUrlRequestExecutor

    @BeforeEach
    fun setUp() {
        executor = GetAuthorizationServerUrlRequestExecutor(request)
    }

    @Test
    fun shouldErrorOutIfAuthConfigIsNotProvided() {
        `when`(request.authConfigs()).thenReturn(emptyList())

        val exception = assertThrows(NoAuthorizationConfigurationException::class.java) {
            executor.execute()
        }

        assertThat(exception.message, `is`("[Authorization Server Url] No authorization configuration found."))
    }

    @Test
    fun shouldReturnAuthorizationServerUrl() {
        `when`(authConfig.configuration).thenReturn(keycloakConfiguration)
        `when`(request.authConfigs()).thenReturn(listOf(authConfig))
        `when`(request.callbackUrl()).thenReturn("https://callback.url")
        `when`(keycloakConfiguration.keycloakApiClient()).thenReturn(keycloakApiClient)
        `when`(keycloakApiClient.authorizationServerUrl("https://callback.url")).thenReturn("https://authorization-server-url")

        val response = executor.execute()

        assertThat(response.responseCode(), `is`(200))
        assertThat(response.responseBody(), startsWith("""{"authorization_server_url":"https://authorization-server-url"}"""))
    }
}
