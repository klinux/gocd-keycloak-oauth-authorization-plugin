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

package cd.go.authorization.keycloak;

import cd.go.authorization.keycloak.models.KeycloakConfiguration;
import cd.go.authorization.keycloak.models.TokenInfo;
import cd.go.authorization.keycloak.requests.UserAuthenticationRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cd.go.authorization.keycloak.KeycloakPlugin.LOG;
import static cd.go.authorization.keycloak.utils.Util.isBlank;
import static cd.go.authorization.keycloak.utils.Util.isNotBlank;
import static java.text.MessageFormat.format;

public class KeycloakApiClient {
    private static final String API_ERROR_MSG = "Api call to `{0}` failed with error: `{1}`";
    private final KeycloakConfiguration keycloakConfiguration;
    private final OkHttpClient httpClient;

    public KeycloakApiClient(KeycloakConfiguration keycloakConfiguration) {
        this(keycloakConfiguration,
                new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()
        );
    }

    public KeycloakApiClient(KeycloakConfiguration keycloakConfiguration, OkHttpClient httpClient) {
        this.keycloakConfiguration = keycloakConfiguration;
        this.httpClient = httpClient;
    }

    public void verifyConnection() throws Exception {
        //TODO:
    }

    public String authorizationServerUrl(String callbackUrl) throws Exception {
        LOG.debug("[KeycloakApiClient] Generating Keycloak oauth url.");
        String realm = keycloakConfiguration.keycloakRealm();

        return HttpUrl.parse(keycloakConfiguration.keycloakEndpoint())
                .newBuilder()
                .addPathSegments("auth")
                .addPathSegments("realms")
                .addPathSegments(realm)
                .addPathSegments("protocol")
                .addPathSegments("openid-connect")
                .addPathSegments("auth")
                .addQueryParameter("client_id", keycloakConfiguration.clientId())
                .addQueryParameter("redirect_uri", callbackUrl)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("scope", "openid profile email groups roles")
                .addQueryParameter("state", UUID.randomUUID().toString())
                .addQueryParameter("nonce", UUID.randomUUID().toString())
                .build().toString();
    }

    public TokenInfo fetchAccessToken(Map<String, String> params) throws Exception {
        final String code = params.get("code");
        if (isBlank(code)) {
            throw new RuntimeException("[KeycloakApiClient] Authorization code must not be null.");
        }

        LOG.debug("[KeycloakApiClient] Fetching access token using authorization code.");
        String realm = keycloakConfiguration.keycloakRealm();

        final String accessTokenUrl = HttpUrl.parse(keycloakConfiguration.keycloakEndpoint())
                .newBuilder()
                .addPathSegments("auth")
                .addPathSegments("realms")
                .addPathSegments(realm)
                .addPathSegments("protocol")
                .addPathSegments("openid-connect")
                .addPathSegments("token")
                .build().toString();

        final FormBody formBody = new FormBody.Builder()
                .add("client_id", keycloakConfiguration.clientId())
                .add("client_secret", keycloakConfiguration.clientSecret())
                .add("code", code)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", CallbackURL.instance().getCallbackURL()).build();

        final Request request = new Request.Builder()
                .url(accessTokenUrl)
                .addHeader("Accept", "application/json")
                .post(formBody)
                .build();

        return executeRequest(request, response -> TokenInfo.fromJSON(response.body().string()));
    }

    public KeycloakUser userProfile(TokenInfo tokenInfo) throws Exception {
        validateTokenInfo(tokenInfo);
        String accessToken = tokenInfo.accessToken();

        // Check status of token
        LOG.debug("[KeycloakApiClient] Token Before: " + tokenInfo.accessToken());
        if (!introspectToken(tokenInfo.accessToken())) {
            LOG.debug("[KeycloakApiClient] Token status: Not Active");
            if (fetchRefreshToken(tokenInfo.refreshToken()).responseCode() == 200) {
                LOG.debug("[KeycloakApiClient] Token After: " + tokenInfo.accessToken());
                accessToken = tokenInfo.accessToken();
            }
        }

        LOG.debug("[KeycloakApiClient] Fetching user profile using access token.");
        String realm = keycloakConfiguration.keycloakRealm();

        final String userProfileUrl = HttpUrl.parse(keycloakConfiguration.keycloakEndpoint())
                .newBuilder()
                .addPathSegments("auth")
                .addPathSegments("realms")
                .addPathSegments(realm)
                .addPathSegments("protocol")
                .addPathSegments("openid-connect")
                .addPathSegments("userinfo")
                .toString();

        final Request request = new Request.Builder()
                .url(userProfileUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        return executeRequest(request, response -> KeycloakUser.fromJSON(response.body().string()));
    }

    private interface Callback<T> {
        T onResponse(Response response) throws IOException;
    }

    private <T> T executeRequest(Request request, Callback<T> callback) throws IOException {
        final Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            final String responseBody = response.body().string();
            final String errorMessage = isNotBlank(responseBody) ? responseBody : response.message();
            throw new RuntimeException(format(API_ERROR_MSG, request.url().encodedPath(), errorMessage));
        }

        return callback.onResponse(response);
    }

    private void validateTokenInfo(TokenInfo tokenInfo) {
        if (tokenInfo == null) {
            throw new RuntimeException("[KeycloakApiClient] TokenInfo must not be null.");
        }
    }

    public Boolean introspectToken(String token) throws Exception {

        LOG.debug("[KeycloakApiClient] Fetching status of the access token.");
        String realm = keycloakConfiguration.keycloakRealm();
        String client = keycloakConfiguration.clientId();
        String secret = keycloakConfiguration.clientSecret();
        String basicEncode = Base64.getEncoder().encodeToString((client + ":" + secret).getBytes());

        final String introspectUrl = HttpUrl.parse(keycloakConfiguration.keycloakEndpoint())
                .newBuilder()
                .addPathSegments("auth")
                .addPathSegments("realms")
                .addPathSegments(realm)
                .addPathSegments("protocol")
                .addPathSegments("openid-connect")
                .addPathSegments("token")
                .addPathSegments("introspect")
                .toString();

        final FormBody formBody = new FormBody.Builder()
                .add("token", token)
                .build();

        final Request request = new Request.Builder()
                .url(introspectUrl)
                .addHeader("Authorization", "Basic " + basicEncode)
                .post(formBody)
                .build();

        KeycloakIntrospectToken getStatus = executeRequest(request, response -> KeycloakIntrospectToken.fromJSON(response.body().string()));

        // Check token status and return true if state ok.
        if (getStatus.getActive()) {
            return true;
        }

        return false;
    }

    public GoPluginApiResponse fetchRefreshToken(String refresh_token) throws Exception {

        LOG.debug("[KeycloakApiClient] Fetching token from refresh token.");
        String realm = keycloakConfiguration.keycloakRealm();
        String client = keycloakConfiguration.clientId();
        String secret = keycloakConfiguration.clientSecret();
        String basicEncode = Base64.getEncoder().encodeToString((client + ":" + secret).getBytes());

        final String refreshTokenUrl = HttpUrl.parse(keycloakConfiguration.keycloakEndpoint())
                .newBuilder()
                .addPathSegments("auth")
                .addPathSegments("realms")
                .addPathSegments(realm)
                .addPathSegments("protocol")
                .addPathSegments("openid-connect")
                .addPathSegments("token")
                .build().toString();

        final FormBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refresh_token)
                .build();

        final Request request = new Request.Builder()
                .url(refreshTokenUrl)
                .addHeader("Authorization", "Basic " + basicEncode)
                .addHeader("Accept", "application/json")
                .post(formBody)
                .build();

        TokenInfo tokenInfo = executeRequest(request, response -> TokenInfo.fromJSON(response.body().string()));

        return DefaultGoPluginApiResponse.success(tokenInfo.toJSON());
    }
}
