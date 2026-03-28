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
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
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

        `when`(keycloakConfiguration.keycloakEndpoint()).thenReturn(server.url("/").toString())
        `when`(keycloakConfiguration.keycloakRealm()).thenReturn("master")
        `when`(keycloakConfiguration.clientId()).thenReturn("client-id")
        `when`(keycloakConfiguration.clientSecret()).thenReturn("client-secret")

        CallbackURL.instance().updateRedirectURL("callback-url")

        keycloakApiClient = KeycloakApiClient(keycloakConfiguration, OkHttpClient())
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldReturnAuthorizationServerUrl() {
        `when`(keycloakConfiguration.keycloakEndpoint()).thenReturn("https://example.com")

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

    @Test
    fun shouldThrowWhenAuthorizationCodeIsBlank() {
        val exception = assertThrows(RuntimeException::class.java) {
            keycloakApiClient.fetchAccessToken(mapOf("code" to ""))
        }
        assertThat(exception.message, `is`("[KeycloakApiClient] Authorization code must not be null."))
    }

    @Test
    fun shouldThrowWhenAuthorizationCodeIsMissing() {
        val exception = assertThrows(RuntimeException::class.java) {
            keycloakApiClient.fetchAccessToken(emptyMap())
        }
        assertThat(exception.message, `is`("[KeycloakApiClient] Authorization code must not be null."))
    }

    @Test
    fun shouldIntrospectActiveToken() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"active": true}"""))

        val result = keycloakApiClient.introspectToken("some-access-token")

        assertTrue(result)

        val request = server.takeRequest()
        assertEquals("POST /realms/master/protocol/openid-connect/token/introspect HTTP/1.1", request.requestLine)
        assertTrue(request.getHeader("Authorization")!!.startsWith("Basic "))
        assertEquals("token=some-access-token", request.body.readUtf8())
    }

    @Test
    fun shouldIntrospectInactiveToken() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"active": false}"""))

        val result = keycloakApiClient.introspectToken("expired-token")

        assertFalse(result)
    }

    @Test
    fun shouldFetchRefreshToken() {
        val tokenResponse = TokenInfo("new-access-token", 3600, "bearer", "new-refresh-token")
        server.enqueue(MockResponse().setResponseCode(200).setBody(tokenResponse.toJSON()))

        val response = keycloakApiClient.fetchRefreshToken("old-refresh-token")

        assertThat(response.responseCode(), `is`(200))

        val request = server.takeRequest()
        assertEquals("POST /realms/master/protocol/openid-connect/token HTTP/1.1", request.requestLine)
        assertTrue(request.getHeader("Authorization")!!.startsWith("Basic "))
        assertEquals("grant_type=refresh_token&refresh_token=old-refresh-token", request.body.readUtf8())
    }

    @Test
    fun shouldThrowOnApiError() {
        server.enqueue(MockResponse().setResponseCode(401).setBody("Unauthorized"))

        val exception = assertThrows(RuntimeException::class.java) {
            keycloakApiClient.fetchAccessToken(mapOf("code" to "bad-code"))
        }
        assertTrue(exception.message!!.contains("Unauthorized"))
    }

    @Test
    fun shouldFetchUserProfile() {
        // First call: introspect (active token)
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"active": true}"""))
        // Second call: userinfo
        val userInfoJson = """
            {
              "email": "user@test.com",
              "name": "Test User",
              "preferred_username": "testuser",
              "sub": "user-uuid-123",
              "groups": ["/admins", "/developers"]
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(userInfoJson))

        val tokenInfo = TokenInfo("access-token", 3600, "bearer", "refresh-token")
        val user = keycloakApiClient.userProfile(tokenInfo)

        assertThat(user.email, `is`("user@test.com"))
        assertThat(user.name, `is`("Test User"))
        assertThat(user.preferredUsername, `is`("testuser"))
        assertThat(user.groups()!!.size, `is`(2))

        // Verify introspect request
        val introspectReq = server.takeRequest()
        assertEquals("POST /realms/master/protocol/openid-connect/token/introspect HTTP/1.1", introspectReq.requestLine)

        // Verify userinfo request
        val userinfoReq = server.takeRequest()
        assertTrue(userinfoReq.requestLine.startsWith("GET /realms/master/protocol/openid-connect/userinfo"))
        assertEquals("Bearer access-token", userinfoReq.getHeader("Authorization"))
    }

    @Test
    fun shouldRefreshTokenWhenIntrospectReturnsInactive() {
        // First call: introspect (inactive token)
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"active": false}"""))
        // Second call: refresh token
        val newTokenInfo = TokenInfo("new-access-token", 3600, "bearer", "new-refresh-token")
        server.enqueue(MockResponse().setResponseCode(200).setBody(newTokenInfo.toJSON()))
        // Third call: userinfo
        val userInfoJson = """{"email": "user@test.com", "name": "Test User"}"""
        server.enqueue(MockResponse().setResponseCode(200).setBody(userInfoJson))

        val tokenInfo = TokenInfo("old-access-token", 3600, "bearer", "old-refresh-token")
        val user = keycloakApiClient.userProfile(tokenInfo)

        assertThat(user.email, `is`("user@test.com"))

        // Verify 3 requests were made: introspect, refresh, userinfo
        assertEquals(3, server.requestCount)
    }
}
