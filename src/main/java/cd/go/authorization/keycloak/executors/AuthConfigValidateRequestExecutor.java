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


import cd.go.authorization.keycloak.annotation.MetadataValidator;
import cd.go.authorization.keycloak.annotation.ValidationResult;
import cd.go.authorization.keycloak.models.KeycloakConfiguration;
import cd.go.authorization.keycloak.requests.AuthConfigValidateRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class AuthConfigValidateRequestExecutor implements RequestExecutor {
    private final AuthConfigValidateRequest request;

    public AuthConfigValidateRequestExecutor(AuthConfigValidateRequest request) {
        this.request = request;
    }

    public GoPluginApiResponse execute() throws Exception {
        final KeycloakConfiguration keycloakConfiguration = request.keycloakConfiguration();
        final ValidationResult validationResult = new MetadataValidator().validate(keycloakConfiguration);

        return DefaultGoPluginApiResponse.success(validationResult.toJSON());
    }
}
