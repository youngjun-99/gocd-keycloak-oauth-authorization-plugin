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

package cd.go.authorization.keycloak.executors;

import cd.go.authorization.keycloak.KeycloakApiClient;
import cd.go.authorization.keycloak.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.keycloak.models.AuthConfig;
import cd.go.authorization.keycloak.models.KeycloakConfiguration;
import cd.go.authorization.keycloak.requests.GetAuthorizationServerUrlRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetAuthorizationServerUrlRequestExecutorTest {

    @Mock
    private GetAuthorizationServerUrlRequest request;
    @Mock
    private AuthConfig authConfig;
    @Mock
    private KeycloakConfiguration keycloakConfiguration;
    @Mock
    private KeycloakApiClient keycloakApiClient;

    private GetAuthorizationServerUrlRequestExecutor executor;

    @BeforeEach
    void setUp() throws Exception {
        executor = new GetAuthorizationServerUrlRequestExecutor(request);
    }

    @Test
    void shouldErrorOutIfAuthConfigIsNotProvided() throws Exception {
        when(request.authConfigs()).thenReturn(Collections.emptyList());

        NoAuthorizationConfigurationException exception = assertThrows(
                NoAuthorizationConfigurationException.class,
                () -> executor.execute()
        );

        assertThat(exception.getMessage(), is("[Authorization Server Url] No authorization configuration found."));
    }

    @Test
    void shouldReturnAuthorizationServerUrl() throws Exception {
        when(authConfig.getConfiguration()).thenReturn(keycloakConfiguration);
        when(request.authConfigs()).thenReturn(Collections.singletonList(authConfig));
        when(request.callbackUrl()).thenReturn("https://callback.url");
        when(keycloakConfiguration.keycloakApiClient()).thenReturn(keycloakApiClient);
        when(keycloakApiClient.authorizationServerUrl("https://callback.url")).thenReturn("https://authorization-server-url");

        final GoPluginApiResponse response = executor.execute();

        assertThat(response.responseCode(), is(200));
        assertThat(response.responseBody(), startsWith("{\"authorization_server_url\":\"https://authorization-server-url\"}"));
    }
}
