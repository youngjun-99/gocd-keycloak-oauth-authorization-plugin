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
import cd.go.authorization.keycloak.utils.Util
import com.google.gson.JsonParser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

class GetAuthConfigViewRequestExecutorTest {

    @Test
    fun shouldRenderTheTemplateInJSON() {
        val response = GetAuthConfigViewRequestExecutor().execute()
        assertThat(response.responseCode(), `is`(200))
        val json = JsonParser.parseString(response.responseBody()).asJsonObject
        assertThat(json.get("template").asString, `is`(Util.readResource("/auth-config.template.html")))
    }

    @Test
    fun allFieldsShouldBePresentInView() {
        val template = Util.readResource("/auth-config.template.html")

        for (field in MetadataHelper.getMetadata(KeycloakConfiguration::class.java)) {
            assertThat(template, containsString("ng-model=\"${field.getKey()}\""))
            assertThat(template, containsString("<span class=\"form_error form-error\" ng-class=\"{'is-visible': GOINPUTNAME[" +
                    "${field.getKey()}].\$error.server}\" ng-show=\"GOINPUTNAME[" +
                    "${field.getKey()}].\$error.server\">{{GOINPUTNAME[" +
                    "${field.getKey()}].\$error.server}}</span>"))
        }
    }
}
