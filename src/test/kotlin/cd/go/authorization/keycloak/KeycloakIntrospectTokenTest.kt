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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import java.math.BigInteger

class KeycloakIntrospectTokenTest {

    @Test
    fun shouldDeserializeActiveToken() {
        val json = """
            {
              "exp": 1700000000,
              "aud": "my-client",
              "active": true
            }
        """.trimIndent()

        val token = KeycloakIntrospectToken.fromJSON(json)

        assertThat(token.active, `is`(true))
        assertThat(token.exp, `is`(BigInteger.valueOf(1700000000)))
        assertThat(token.getAudience(), `is`("my-client" as Any))
    }

    @Test
    fun shouldDeserializeInactiveToken() {
        val json = """{"active": false}"""

        val token = KeycloakIntrospectToken.fromJSON(json)

        assertThat(token.active, `is`(false))
        assertThat(token.exp, `is`(nullValue()))
    }

    @Test
    fun shouldDeserializeTokenWithArrayAudience() {
        val json = """
            {
              "active": true,
              "aud": ["client-1", "client-2"]
            }
        """.trimIndent()

        val token = KeycloakIntrospectToken.fromJSON(json)

        assertThat(token.active, `is`(true))
    }

    @Test
    fun shouldSerializeToJSON() {
        val token = KeycloakIntrospectToken.fromJSON("""{"active": true, "exp": 123456}""")
        val json = token.toJSON()

        val deserialized = KeycloakIntrospectToken.fromJSON(json)
        assertThat(deserialized.active, `is`(true))
        assertThat(deserialized.exp, `is`(BigInteger.valueOf(123456)))
    }
}
