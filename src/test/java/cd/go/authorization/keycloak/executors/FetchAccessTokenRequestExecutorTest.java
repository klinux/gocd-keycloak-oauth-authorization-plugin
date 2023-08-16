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
import cd.go.authorization.keycloak.models.TokenInfo;
import cd.go.authorization.keycloak.requests.FetchAccessTokenRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FetchAccessTokenRequestExecutorTest {
    @Mock
    private FetchAccessTokenRequest request;
    @Mock
    private AuthConfig authConfig;
    @Mock
    private KeycloakConfiguration keycloakConfiguration;
    @Mock
    private KeycloakApiClient keycloakApiClient;
    private FetchAccessTokenRequestExecutor executor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(authConfig.getConfiguration()).thenReturn(keycloakConfiguration);
        when(keycloakConfiguration.keycloakApiClient()).thenReturn(keycloakApiClient);

        executor = new FetchAccessTokenRequestExecutor(request);
    }

    @Test
    public void shouldErrorOutIfAuthConfigIsNotProvided() throws Exception {
        when(request.authConfigs()).thenReturn(Collections.emptyList());

        Throwable thrown = assertThrows(NoAuthorizationConfigurationException.class, () -> executor.execute());
        assertThat(thrown.getMessage(), is("[Get Access Token] No authorization configuration found."));
    }

    @Test
    public void shouldFetchAccessToken() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("31239032-xycs.xddasdasdasda", 7200, "foo-type", "refresh-xysaddasdjlascdas");

        when(request.authConfigs()).thenReturn(Collections.singletonList(authConfig));
        when(request.requestParameters()).thenReturn(Collections.singletonMap("code", "code-received-in-previous-step"));
        when(keycloakApiClient.fetchAccessToken(request.requestParameters())).thenReturn(tokenInfo);

        final GoPluginApiResponse response = executor.execute();


        final String expectedJSON = "{\n" +
                "  \"access_token\": \"31239032-xycs.xddasdasdasda\",\n" +
                "  \"expires_in\": 7200,\n" +
                "  \"token_type\": \"foo-type\",\n" +
                "  \"refresh_token\": \"refresh-xysaddasdjlascdas\"\n" +
                "}";

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}
