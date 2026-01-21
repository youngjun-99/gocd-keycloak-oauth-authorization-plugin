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

package cd.go.authorization.keycloak;

import cd.go.authorization.keycloak.models.KeycloakConfiguration;
import cd.go.authorization.keycloak.models.TokenInfo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KeycloakApiClientTest {

    @Mock
    private KeycloakConfiguration KeycloakConfiguration;
    private MockWebServer server;
    private KeycloakApiClient KeycloakApiClient;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();

        when(KeycloakConfiguration.keycloakEndpoint()).thenReturn("https://example.com");
        when(KeycloakConfiguration.keycloakRealm()).thenReturn("master");
        when(KeycloakConfiguration.clientId()).thenReturn("client-id");
        when(KeycloakConfiguration.clientSecret()).thenReturn("client-secret");

        CallbackURL.instance().updateRedirectURL("callback-url");

        KeycloakApiClient = new KeycloakApiClient(KeycloakConfiguration);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void shouldReturnAuthorizationServerUrl() throws Exception {
        final String authorizationServerUrl = KeycloakApiClient.authorizationServerUrl("call-back-url");

        assertThat(authorizationServerUrl, startsWith("https://example.com/realms/master/protocol/openid-connect/auth?client_id=client-id&redirect_uri=call-back-url&response_type=code&scope=openid%20profile%20email%20groups%20roles&state="));
    }

    @Test
    void shouldFetchTokenInfoUsingAuthorizationCode() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new TokenInfo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9", 3600, "bearer", "refresh-token").toJSON()));

        when(KeycloakConfiguration.keycloakEndpoint()).thenReturn(server.url("/").toString());

        final TokenInfo tokenInfo = KeycloakApiClient.fetchAccessToken(Collections.singletonMap("code", "some-code"));

        assertThat(tokenInfo.accessToken(), is("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9"));

        RecordedRequest request = server.takeRequest();
        assertEquals("POST /realms/master/protocol/openid-connect/token HTTP/1.1", request.getRequestLine());
        assertEquals("application/x-www-form-urlencoded", request.getHeader("Content-Type"));
        assertEquals("client_id=client-id&client_secret=client-secret&code=some-code&grant_type=authorization_code&redirect_uri=callback-url", request.getBody().readUtf8());
    }

}
