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

import cd.go.authorization.keycloak.annotation.MetadataHelper
import cd.go.authorization.keycloak.models.KeycloakConfiguration
import com.google.gson.JsonParser
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class GetAuthConfigMetadataRequestExecutorTest {

    @Test
    fun shouldSerializeAllFields() {
        val response = GetAuthConfigMetadataRequestExecutor().execute()
        val jsonArray = JsonParser.parseString(response.responseBody()).asJsonArray
        assertEquals(jsonArray.size(), MetadataHelper.getMetadata(KeycloakConfiguration::class.java).size)
    }

    @Test
    fun assertJsonStructure() {
        val response = GetAuthConfigMetadataRequestExecutor().execute()

        assertThat(response.responseCode(), `is`(200))

        val expectedJSON = """
            [
              {"key": "KeycloakEndpoint", "metadata": {"required": true, "secure": false}},
              {"key": "KeycloakRealm", "metadata": {"required": true, "secure": false}},
              {"key": "ClientId", "metadata": {"required": true, "secure": false}},
              {"key": "ClientSecret", "metadata": {"required": true, "secure": true}}
            ]
        """.trimIndent()

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true)
    }
}
