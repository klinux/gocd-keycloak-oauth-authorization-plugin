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

import cd.go.authorization.keycloak.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.keycloak.models.KeycloakConfiguration;
import cd.go.authorization.keycloak.models.TokenInfo;
import cd.go.authorization.keycloak.requests.FetchAccessTokenRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class FetchAccessTokenRequestExecutor implements RequestExecutor {
    private final FetchAccessTokenRequest request;

    public FetchAccessTokenRequestExecutor(FetchAccessTokenRequest request) {
        this.request = request;
    }

    public GoPluginApiResponse execute() throws Exception {
        if (request.authConfigs() == null || request.authConfigs().isEmpty()) {
            throw new NoAuthorizationConfigurationException("[Get Access Token] No authorization configuration found.");
        }

        final KeycloakConfiguration configuration = request.authConfigs().get(0).getConfiguration();
        final TokenInfo tokenInfo = configuration.keycloakApiClient().fetchAccessToken(request.requestParameters());

        return DefaultGoPluginApiResponse.success(tokenInfo.toJSON());
    }
}
