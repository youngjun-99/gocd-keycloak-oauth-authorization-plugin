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

package cd.go.authorization.keycloak.requests

import cd.go.authorization.keycloak.executors.GetAuthorizationServerUrlRequestExecutor
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class GetAuthorizationServerUrlRequestTest {

    @Mock
    private lateinit var apiRequest: GoPluginApiRequest

    @Test
    fun shouldDeserializeGoPluginApiRequestToGetAuthorizationServerUrlRequest() {
        val responseBody = """
            {
              "authorization_server_callback_url": "https://redirect.url",
              "auth_configs": [
                {
                  "id": "keycloak-config",
                  "configuration": {
                    "KeycloakEndpoint": "https://example.com",
                    "ClientId": "client-id",
                    "ClientSecret": "client-secret"
                  }
                }
              ]
            }
        """.trimIndent()

        `when`(apiRequest.requestBody()).thenReturn(responseBody)

        val request = GetAuthorizationServerUrlRequest.from(apiRequest)

        assertThat(request.authConfigs(), hasSize(1))
        assertThat(request.executor(), instanceOf(GetAuthorizationServerUrlRequestExecutor::class.java))

        val authConfig = request.authConfigs()[0]

        assertThat(request.callbackUrl(), `is`("https://redirect.url"))
        assertThat(authConfig.id, `is`("keycloak-config"))
        assertThat(authConfig.configuration!!.keycloakEndpoint(), `is`("https://example.com"))
        assertThat(authConfig.configuration!!.clientId(), `is`("client-id"))
        assertThat(authConfig.configuration!!.clientSecret(), `is`("client-secret"))
    }
}
