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

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class KeycloakUserTest {

    @Test
    public void shouldDeserializeJSON() throws Exception {
        final KeycloakUser keycloakUser = KeycloakUser.fromJSON("{\n" +
                " \"email\": \"foo@example.com\",\n" +
                " \"email_verified\": true,\n" +
                " \"name\": \"Foo Bar\",\n" +
                " \"given_name\": \"Bar\",\n" +
                " \"family_name\": \"Foo\",\n" +
                " \"locale\": \"en\",\n" +
                " \"preferred_username\": \"foo@example.com\",\n" +
                " \"sub\": \"00uea8uu\",\n" +
                " \"updated_at\": 1520793947,\n" +
                " \"zoneinfo\": \"America/Los_Angeles\"\n" +
                "}");

        assertThat(keycloakUser.getEmail(), is("foo@example.com"));
        assertThat(keycloakUser.isVerifiedEmail(), is(true));
        assertThat(keycloakUser.getName(), is("Foo Bar"));
        assertThat(keycloakUser.getGivenName(), is("Bar"));
        assertThat(keycloakUser.getFamilyName(), is("Foo"));
        assertThat(keycloakUser.getLocale(), is("en"));
        assertThat(keycloakUser.getPreferredUsername(), is("foo@example.com"));
        assertThat(keycloakUser.getSub(), is("00uea8uu"));
        assertThat(keycloakUser.getUpdatedAt(), is(1520793947));
        assertThat(keycloakUser.getZoneInfo(), is("America/Los_Angeles"));
    }
}
