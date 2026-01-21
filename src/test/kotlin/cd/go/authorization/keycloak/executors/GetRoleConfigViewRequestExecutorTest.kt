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
import cd.go.authorization.keycloak.models.KeycloakRoleConfiguration
import cd.go.authorization.keycloak.utils.Util
import com.google.gson.JsonParser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

class GetRoleConfigViewRequestExecutorTest {

    @Test
    fun allFieldsShouldBePresentInView() {
        val template = Util.readResource("/role-config.template.html")
        val document = Jsoup.parse(template)

        val metadataList = MetadataHelper.getMetadata(KeycloakRoleConfiguration::class.java)
        for (field in metadataList) {
            val inputFieldForKey = document.getElementsByAttributeValue("ng-model", field.getKey())
            assertThat(inputFieldForKey, hasSize(1))

            val spanToShowError = document.getElementsByAttributeValue("ng-class", "{'is-visible': GOINPUTNAME[${field.getKey()}].\$error.server}")
            assertThat(spanToShowError, hasSize(1))
            assertThat(spanToShowError.attr("ng-show"), `is`("GOINPUTNAME[${field.getKey()}].\$error.server"))
            assertThat(spanToShowError.text(), `is`("{{GOINPUTNAME[${field.getKey()}].\$error.server}}"))
        }

        val inputs = document.select("textarea,input,select")
        assertThat("should contains only inputs that defined in KeycloakRoleConfiguration", inputs, hasSize(metadataList.size))
    }

    @Test
    fun shouldRenderTheTemplateInJSON() {
        val response = GetRoleConfigViewRequestExecutor().execute()
        assertThat(response.responseCode(), `is`(200))
        val json = JsonParser.parseString(response.responseBody()).asJsonObject
        assertThat(json.get("template").asString, `is`(Util.readResource("/role-config.template.html")))
    }
}
