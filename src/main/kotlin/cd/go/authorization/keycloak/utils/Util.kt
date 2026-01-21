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

package cd.go.authorization.keycloak.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.util.Properties

object Util {
    @JvmField
    val GSON: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    @JvmStatic
    fun readResource(resourceFile: String): String {
        return String(readResourceBytes(resourceFile), StandardCharsets.UTF_8)
    }

    @JvmStatic
    fun readResourceBytes(resourceFile: String): ByteArray {
        return Util::class.java.getResourceAsStream(resourceFile)?.use { input ->
            val buffer = ByteArray(8192)
            val output = ByteArrayOutputStream()
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            output.toByteArray()
        } ?: throw RuntimeException("Could not find resource $resourceFile")
    }

    @JvmStatic
    fun pluginId(): String {
        val s = readResource("/plugin.properties")
        val properties = Properties()
        properties.load(StringReader(s))
        return properties["id"] as String
    }

    @JvmStatic
    fun splitIntoLinesAndTrimSpaces(lines: String?): List<String> {
        if (isBlank(lines)) {
            return emptyList()
        }
        return lines!!.split("\\s*[\r\n]+\\s*".toRegex())
    }

    @JvmStatic
    fun listFromCommaSeparatedString(str: String?): List<String> {
        if (isBlank(str)) {
            return emptyList()
        }
        return str!!.split("\\s*,\\s*".toRegex())
    }

    @JvmStatic
    fun toLowerCase(str: String?): String? {
        return if (isBlank(str)) str else str!!.lowercase()
    }

    @JvmStatic
    fun isBlank(cs: CharSequence?): Boolean {
        if (cs == null || cs.isEmpty()) {
            return true
        }
        for (i in cs.indices) {
            if (!Character.isWhitespace(cs[i])) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun isNotBlank(cs: CharSequence?): Boolean {
        return !isBlank(cs)
    }
}
