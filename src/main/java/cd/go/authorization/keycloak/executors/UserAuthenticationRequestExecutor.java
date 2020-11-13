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

package cd.go.authorization.keycloak.executors;

import cd.go.authorization.keycloak.KeycloakApiClient;
import cd.go.authorization.keycloak.KeycloakAuthorizer;
import cd.go.authorization.keycloak.KeycloakUser;
import cd.go.authorization.keycloak.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.keycloak.models.AuthConfig;
import cd.go.authorization.keycloak.models.KeycloakConfiguration;
import cd.go.authorization.keycloak.models.User;
import cd.go.authorization.keycloak.requests.UserAuthenticationRequest;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

import static cd.go.authorization.keycloak.KeycloakPlugin.LOG;

public class UserAuthenticationRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();
    private final UserAuthenticationRequest request;
    private final KeycloakAuthorizer keycloakAuthorizer;

    public UserAuthenticationRequestExecutor(UserAuthenticationRequest request) {
        this(request, new KeycloakAuthorizer());
    }

    UserAuthenticationRequestExecutor(UserAuthenticationRequest request, KeycloakAuthorizer keycloakAuthorizer) {
        this.request = request;
        this.keycloakAuthorizer = keycloakAuthorizer;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        if (request.authConfigs() == null || request.authConfigs().isEmpty()) {
            throw new NoAuthorizationConfigurationException("[Authenticate] No authorization configuration found.");
        }

        final AuthConfig authConfig = request.authConfigs().get(0);
        final KeycloakConfiguration configuration = request.authConfigs().get(0).getConfiguration();
        final KeycloakApiClient keycloakApiClient = configuration.keycloakApiClient();
        final KeycloakUser keycloakUser = keycloakApiClient.userProfile(request.tokenInfo());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("user", new User(keycloakUser));
        userMap.put("roles", keycloakAuthorizer.authorize(keycloakUser, authConfig, request.roles()));

        return DefaultGoPluginApiResponse.success(GSON.toJson(userMap));
    }
}
