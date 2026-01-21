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

import cd.go.authorization.keycloak.utils.Util
import com.google.gson.JsonParser
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.util.Base64

class GetPluginIconRequestExecutorTest {

    @Test
    fun rendersIconInBase64() {
        val response = GetPluginIconRequestExecutor().execute()
        val json = JsonParser.parseString(response.responseBody()).asJsonObject
        assertThat(json.size(), `is`(2))
        assertThat(json.get("content_type").asString, `is`("image/png"))
        assertThat(Util.readResourceBytes("/keycloak.png"), `is`(Base64.getDecoder().decode(json.get("data").asString)))
    }
}
