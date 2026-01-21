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
import cd.go.authorization.keycloak.KeycloakUser
import cd.go.authorization.keycloak.exceptions.NoAuthorizationConfigurationException
import cd.go.authorization.keycloak.models.AuthConfig
import cd.go.authorization.keycloak.models.KeycloakConfiguration
import cd.go.authorization.keycloak.models.TokenInfo
import cd.go.authorization.keycloak.requests.UserAuthenticationRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import org.skyscreamer.jsonassert.JSONAssert

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserAuthenticationRequestExecutorTest {

    @Mock
    private lateinit var request: UserAuthenticationRequest
    @Mock
    private lateinit var authConfig: AuthConfig
    @Mock
    private lateinit var keycloakConfiguration: KeycloakConfiguration
    @Mock
    private lateinit var keycloakApiClient: KeycloakApiClient

    private lateinit var executor: UserAuthenticationRequestExecutor

    @BeforeEach
    fun setUp() {
        `when`(authConfig.configuration).thenReturn(keycloakConfiguration)
        `when`(keycloakConfiguration.keycloakApiClient()).thenReturn(keycloakApiClient)
        executor = UserAuthenticationRequestExecutor(request)
    }

    @Test
    fun shouldErrorOutIfAuthConfigIsNotProvided() {
        `when`(request.authConfigs()).thenReturn(emptyList())

        val exception = assertThrows(NoAuthorizationConfigurationException::class.java) {
            executor.execute()
        }

        assertThat(exception.message, `is`("[Authenticate] No authorization configuration found."))
    }

    @Test
    fun shouldAuthenticate() {
        val tokenInfo = TokenInfo("31239032-xycs.xddasdasdasda", 7200, "foo-type", "refresh-xysaddasdjlascdas")

        `when`(request.authConfigs()).thenReturn(listOf(authConfig))
        `when`(request.tokenInfo()).thenReturn(tokenInfo)
        `when`(keycloakApiClient.userProfile(tokenInfo)).thenReturn(KeycloakUser("foo@bar.com", "Foo Bar"))

        val response = executor.execute()

        val expectedJSON = """
            {
              "roles": [],
              "user": {
                "username": "foo@bar.com",
                "display_name": "Foo Bar",
                "email": "foo@bar.com"
              }
            }
        """.trimIndent()

        assertThat(response.responseCode(), `is`(200))
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true)
    }
}
