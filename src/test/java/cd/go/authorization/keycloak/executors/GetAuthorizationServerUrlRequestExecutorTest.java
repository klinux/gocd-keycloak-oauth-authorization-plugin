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
import cd.go.authorization.keycloak.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.keycloak.models.AuthConfig;
import cd.go.authorization.keycloak.models.KeycloakConfiguration;
import cd.go.authorization.keycloak.requests.GetAuthorizationServerUrlRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GetAuthorizationServerUrlRequestExecutorTest {
    @Mock
    private GetAuthorizationServerUrlRequest request;
    @Mock
    private AuthConfig authConfig;
    @Mock
    private KeycloakConfiguration keycloakConfiguration;
    @Mock
    private KeycloakApiClient keycloakApiClient;

    private GetAuthorizationServerUrlRequestExecutor executor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        executor = new GetAuthorizationServerUrlRequestExecutor(request);
    }

    @Test
    public void shouldErrorOutIfAuthConfigIsNotProvided() throws Exception {
        when(request.authConfigs()).thenReturn(Collections.emptyList());

        Throwable thrown = assertThrows(NoAuthorizationConfigurationException.class, () -> executor.execute());
        assertThat(thrown.getMessage(), is("[Authorization Server Url] No authorization configuration found."));
    }

    @Test
    public void shouldReturnAuthorizationServerUrl() throws Exception {
        when(authConfig.getConfiguration()).thenReturn(keycloakConfiguration);
        when(request.authConfigs()).thenReturn(Collections.singletonList(authConfig));
        when(keycloakConfiguration.keycloakApiClient()).thenReturn(keycloakApiClient);
        when(keycloakApiClient.authorizationServerUrl(request.callbackUrl())).thenReturn("https://authorization-server-url");

        final GoPluginApiResponse response = executor.execute();

        assertThat(response.responseCode(), is(200));
        assertThat(response.responseBody(), startsWith("{\"authorization_server_url\":\"https://authorization-server-url\"}"));
    }
}
