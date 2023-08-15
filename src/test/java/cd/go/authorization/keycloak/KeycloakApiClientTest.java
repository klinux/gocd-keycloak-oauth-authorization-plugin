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
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KeycloakApiClientTest {

    @Mock
    private KeycloakConfiguration KeycloakConfiguration;
    private MockWebServer server;
    private KeycloakApiClient KeycloakApiClient;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        server = new MockWebServer();
        server.start();

        when(KeycloakConfiguration.keycloakEndpoint()).thenReturn("https://example.com");
        when(KeycloakConfiguration.keycloakRealm()).thenReturn("master");
        when(KeycloakConfiguration.clientId()).thenReturn("client-id");
        when(KeycloakConfiguration.clientSecret()).thenReturn("client-secret");

        CallbackURL.instance().updateRedirectURL("callback-url");

        KeycloakApiClient = new KeycloakApiClient(KeycloakConfiguration);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void shouldReturnAuthorizationServerUrl() throws Exception {
        final String authorizationServerUrl = KeycloakApiClient.authorizationServerUrl("call-back-url");

        assertThat(authorizationServerUrl, startsWith("https://example.com/auth/realms/master/protocol/openid-connect/auth?client_id=client-id&redirect_uri=call-back-url&response_type=code&scope=openid%20profile%20email%20groups%20roles&state="));
    }

    @Test
    public void shouldFetchTokenInfoUsingAuthorizationCode() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new TokenInfo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9", 3600, "bearer", "refresh-token").toJSON()));

        when(KeycloakConfiguration.keycloakEndpoint()).thenReturn(server.url("/").toString());

        final TokenInfo tokenInfo = KeycloakApiClient.fetchAccessToken(Collections.singletonMap("code", "some-code"));

        assertThat(tokenInfo.accessToken(), is("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9"));

        RecordedRequest request = server.takeRequest();
        assertEquals("POST /auth/realms/master/protocol/openid-connect/token HTTP/1.1", request.getRequestLine());
        assertEquals("application/x-www-form-urlencoded", request.getHeader("Content-Type"));
        assertEquals("client_id=client-id&client_secret=client-secret&code=some-code&grant_type=authorization_code&redirect_uri=callback-url", request.getBody().readUtf8());
    }

}
