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

import cd.go.authorization.keycloak.requests.AuthConfigValidateRequest
import com.google.gson.Gson
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class AuthConfigValidateRequestExecutorTest {

    @Test
    fun shouldValidateMandatoryKeys() {
        val request = mock(GoPluginApiRequest::class.java)
        `when`(request.requestBody()).thenReturn(Gson().toJson(emptyMap<String, String>()))

        val response = AuthConfigValidateRequest.from(request).execute()

        val expectedJSON = """
            [
              {"message": "KeycloakEndpoint must not be blank.", "key": "KeycloakEndpoint"},
              {"message": "KeycloakRealm must not be blank.", "key": "KeycloakRealm"},
              {"message": "ClientId must not be blank.", "key": "ClientId"},
              {"message": "ClientSecret must not be blank.", "key": "ClientSecret"}
            ]
        """.trimIndent()

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE)
    }
}
