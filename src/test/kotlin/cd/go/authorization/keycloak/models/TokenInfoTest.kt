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

package cd.go.authorization.keycloak.models

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class TokenInfoTest {

    @Test
    fun shouldDeserializeJSON() {
        val tokenInfo = TokenInfo.fromJSON("""
            {
              "access_token": "31239032-xycs.xddasdasdasda",
              "expires_in": 7200,
              "token_type": "foo-type",
              "refresh_token": "refresh-xysaddasdjlascdas"
            }
        """.trimIndent())

        assertThat(tokenInfo.accessToken(), `is`("31239032-xycs.xddasdasdasda"))
        assertThat(tokenInfo.expiresIn(), `is`(7200L))
        assertThat(tokenInfo.tokenType(), `is`("foo-type"))
        assertThat(tokenInfo.refreshToken(), `is`("refresh-xysaddasdjlascdas"))
    }

    @Test
    fun shouldSerializeToJSON() {
        val tokenInfo = TokenInfo("31239032-xycs.xddasdasdasda", 7200, "foo-type", "refresh-xysaddasdjlascdas")

        val expectedJSON = """
            {
              "access_token": "31239032-xycs.xddasdasdasda",
              "expires_in": 7200,
              "token_type": "foo-type",
              "refresh_token": "refresh-xysaddasdjlascdas"
            }
        """.trimIndent()

        JSONAssert.assertEquals(expectedJSON, tokenInfo.toJSON(), true)
    }
}
