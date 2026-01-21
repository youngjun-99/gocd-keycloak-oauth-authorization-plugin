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

import cd.go.authorization.keycloak.models.KeycloakConfiguration
import cd.go.authorization.keycloak.models.TokenInfo
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
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
class KeycloakApiClientTest {

    @Mock
    private lateinit var keycloakConfiguration: KeycloakConfiguration

    private lateinit var server: MockWebServer
    private lateinit var keycloakApiClient: KeycloakApiClient

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()

        `when`(keycloakConfiguration.keycloakEndpoint()).thenReturn("https://example.com")
        `when`(keycloakConfiguration.keycloakRealm()).thenReturn("master")
        `when`(keycloakConfiguration.clientId()).thenReturn("client-id")
        `when`(keycloakConfiguration.clientSecret()).thenReturn("client-secret")

        CallbackURL.instance().updateRedirectURL("callback-url")

        keycloakApiClient = KeycloakApiClient(keycloakConfiguration)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldReturnAuthorizationServerUrl() {
        val authorizationServerUrl = keycloakApiClient.authorizationServerUrl("call-back-url")

        assertThat(
            authorizationServerUrl,
            startsWith("https://example.com/realms/master/protocol/openid-connect/auth?client_id=client-id&redirect_uri=call-back-url&response_type=code&scope=openid%20profile%20email%20groups%20roles&state=")
        )
    }

    @Test
    fun shouldFetchTokenInfoUsingAuthorizationCode() {
        val tokenInfo = TokenInfo(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9",
            3600,
            "bearer",
            "refresh-token"
        )
        server.enqueue(MockResponse().setResponseCode(200).setBody(tokenInfo.toJSON()))

        `when`(keycloakConfiguration.keycloakEndpoint()).thenReturn(server.url("/").toString())

        val result = keycloakApiClient.fetchAccessToken(mapOf("code" to "some-code"))

        assertThat(
            result.accessToken(),
            `is`("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9")
        )

        val request = server.takeRequest()
        assertEquals("POST /realms/master/protocol/openid-connect/token HTTP/1.1", request.requestLine)
        assertEquals("application/x-www-form-urlencoded", request.getHeader("Content-Type"))
        assertEquals(
            "client_id=client-id&client_secret=client-secret&code=some-code&grant_type=authorization_code&redirect_uri=callback-url",
            request.body.readUtf8()
        )
    }
}
