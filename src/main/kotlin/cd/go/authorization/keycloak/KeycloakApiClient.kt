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

import cd.go.authorization.keycloak.models.KeycloakConfiguration
import cd.go.authorization.keycloak.models.TokenInfo
import cd.go.authorization.keycloak.utils.Util.isBlank
import cd.go.authorization.keycloak.utils.Util.isNotBlank
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException
import java.text.MessageFormat.format
import java.util.*
import java.util.concurrent.TimeUnit

class KeycloakApiClient(
    private val keycloakConfiguration: KeycloakConfiguration,
    private val httpClient: OkHttpClient
) {
    constructor(keycloakConfiguration: KeycloakConfiguration) : this(
        keycloakConfiguration,
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    )
    companion object {
        private const val API_ERROR_MSG = "Api call to `{0}` failed with error: `{1}`"
    }

    fun verifyConnection() {
        // TODO:
    }

    fun authorizationServerUrl(callbackUrl: String): String {
        KeycloakPlugin.LOG.debug("[KeycloakApiClient] Generating Keycloak oauth url.")
        val realm = keycloakConfiguration.keycloakRealm()

        return keycloakConfiguration.keycloakEndpoint()!!.toHttpUrl()
            .newBuilder()
            .addPathSegments("realms")
            .addPathSegments(realm!!)
            .addPathSegments("protocol")
            .addPathSegments("openid-connect")
            .addPathSegments("auth")
            .addQueryParameter("client_id", keycloakConfiguration.clientId())
            .addQueryParameter("redirect_uri", callbackUrl)
            .addQueryParameter("response_type", "code")
            .addQueryParameter("scope", "openid profile email groups roles")
            .addQueryParameter("state", UUID.randomUUID().toString())
            .addQueryParameter("nonce", UUID.randomUUID().toString())
            .build().toString()
    }

    fun fetchAccessToken(params: Map<String, String>): TokenInfo {
        val code = params["code"]
        if (isBlank(code)) {
            throw RuntimeException("[KeycloakApiClient] Authorization code must not be null.")
        }

        KeycloakPlugin.LOG.debug("[KeycloakApiClient] Fetching access token using authorization code.")
        val realm = keycloakConfiguration.keycloakRealm()

        val accessTokenUrl = keycloakConfiguration.keycloakEndpoint()!!.toHttpUrl()
            .newBuilder()
            .addPathSegments("realms")
            .addPathSegments(realm!!)
            .addPathSegments("protocol")
            .addPathSegments("openid-connect")
            .addPathSegments("token")
            .build().toString()

        val formBody = FormBody.Builder()
            .add("client_id", keycloakConfiguration.clientId()!!)
            .add("client_secret", keycloakConfiguration.clientSecret()!!)
            .add("code", code!!)
            .add("grant_type", "authorization_code")
            .add("redirect_uri", CallbackURL.instance().callbackURL!!)
            .build()

        val request = Request.Builder()
            .url(accessTokenUrl)
            .addHeader("Accept", "application/json")
            .post(formBody)
            .build()

        return executeRequest(request) { response -> TokenInfo.fromJSON(response.body!!.string()) }
    }

    fun userProfile(tokenInfo: TokenInfo): KeycloakUser {
        validateTokenInfo(tokenInfo)
        var accessToken = tokenInfo.accessToken()

        KeycloakPlugin.LOG.debug("[KeycloakApiClient] Token Before: ${tokenInfo.accessToken()}")
        if (!introspectToken(tokenInfo.accessToken()!!)) {
            KeycloakPlugin.LOG.debug("[KeycloakApiClient] Token status: Not Active")
            if (fetchRefreshToken(tokenInfo.refreshToken()!!).responseCode() == 200) {
                KeycloakPlugin.LOG.debug("[KeycloakApiClient] Token After: ${tokenInfo.accessToken()}")
                accessToken = tokenInfo.accessToken()
            }
        }

        KeycloakPlugin.LOG.debug("[KeycloakApiClient] Fetching user profile using access token.")
        val realm = keycloakConfiguration.keycloakRealm()

        val userProfileUrl = keycloakConfiguration.keycloakEndpoint()!!.toHttpUrl()
            .newBuilder()
            .addPathSegments("realms")
            .addPathSegments(realm!!)
            .addPathSegments("protocol")
            .addPathSegments("openid-connect")
            .addPathSegments("userinfo")
            .toString()

        val request = Request.Builder()
            .url(userProfileUrl)
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()

        return executeRequest(request) { response -> KeycloakUser.fromJSON(response.body!!.string()) }
    }

    private fun interface Callback<T> {
        fun onResponse(response: Response): T
    }

    private fun <T> executeRequest(request: Request, callback: Callback<T>): T {
        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            val errorMessage = if (isNotBlank(responseBody)) responseBody else response.message
            throw RuntimeException(format(API_ERROR_MSG, request.url.encodedPath, errorMessage))
        }

        return callback.onResponse(response)
    }

    private fun validateTokenInfo(tokenInfo: TokenInfo?) {
        if (tokenInfo == null) {
            throw RuntimeException("[KeycloakApiClient] TokenInfo must not be null.")
        }
    }

    fun introspectToken(token: String): Boolean {
        KeycloakPlugin.LOG.debug("[KeycloakApiClient] Fetching status of the access token.")
        val realm = keycloakConfiguration.keycloakRealm()
        val client = keycloakConfiguration.clientId()
        val secret = keycloakConfiguration.clientSecret()
        val basicEncode = Base64.getEncoder().encodeToString("$client:$secret".toByteArray())

        val introspectUrl = keycloakConfiguration.keycloakEndpoint()!!.toHttpUrl()
            .newBuilder()
            .addPathSegments("realms")
            .addPathSegments(realm!!)
            .addPathSegments("protocol")
            .addPathSegments("openid-connect")
            .addPathSegments("token")
            .addPathSegments("introspect")
            .toString()

        val formBody = FormBody.Builder()
            .add("token", token)
            .build()

        val request = Request.Builder()
            .url(introspectUrl)
            .addHeader("Authorization", "Basic $basicEncode")
            .post(formBody)
            .build()

        val getStatus = executeRequest(request) { response -> KeycloakIntrospectToken.fromJSON(response.body!!.string()) }

        return getStatus.active
    }

    fun fetchRefreshToken(refreshToken: String): GoPluginApiResponse {
        KeycloakPlugin.LOG.debug("[KeycloakApiClient] Fetching token from refresh token.")
        val realm = keycloakConfiguration.keycloakRealm()
        val client = keycloakConfiguration.clientId()
        val secret = keycloakConfiguration.clientSecret()
        val basicEncode = Base64.getEncoder().encodeToString("$client:$secret".toByteArray())

        val refreshTokenUrl = keycloakConfiguration.keycloakEndpoint()!!.toHttpUrl()
            .newBuilder()
            .addPathSegments("realms")
            .addPathSegments(realm!!)
            .addPathSegments("protocol")
            .addPathSegments("openid-connect")
            .addPathSegments("token")
            .build().toString()

        val formBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .build()

        val request = Request.Builder()
            .url(refreshTokenUrl)
            .addHeader("Authorization", "Basic $basicEncode")
            .addHeader("Accept", "application/json")
            .post(formBody)
            .build()

        val tokenInfo = executeRequest(request) { response -> TokenInfo.fromJSON(response.body!!.string()) }

        return DefaultGoPluginApiResponse.success(tokenInfo.toJSON())
    }
}
