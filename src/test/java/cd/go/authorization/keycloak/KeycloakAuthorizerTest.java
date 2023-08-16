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

import cd.go.authorization.keycloak.models.*;
import cd.go.authorization.keycloak.KeycloakUser;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class KeycloakAuthorizerTest {

    private KeycloakAuthorizer authorizer;
    private KeycloakUser loggedInUser;
    private MembershipChecker membershipChecker;
    private AuthConfig authConfig;

    @Before
    public void setUp() throws Exception {
        membershipChecker = mock(MembershipChecker.class);
        loggedInUser = mock(KeycloakUser.class);
        authConfig = mock(AuthConfig.class);

        authorizer = new KeycloakAuthorizer(membershipChecker);
    }

    @Test
    public void shouldReturnEmptyListIfNoRoleConfiguredForGivenAuthConfig() throws Exception {
        final List<String> assignedRoles = authorizer.authorize(loggedInUser, authConfig, Collections.emptyList());

        assertThat(assignedRoles, hasSize(0));
        verifyZeroInteractions(authConfig);
        verifyZeroInteractions(membershipChecker);
    }

    @Test
    public void shouldAssignRoleIfUserIsAMemberOfAtLeastOneGroup() throws Exception {
        final Role role = mock(Role.class);
        final KeycloakRoleConfiguration roleConfiguration = mock(KeycloakRoleConfiguration.class);

        when(role.name()).thenReturn("admin");
        when(role.roleConfiguration()).thenReturn(roleConfiguration);
        when(roleConfiguration.groups()).thenReturn(singletonList("group-1"));
        when(membershipChecker.isAMemberOfAtLeastOneGroup(loggedInUser, authConfig, roleConfiguration.groups())).thenReturn(true);

        final List<String> assignedRoles = authorizer.authorize(loggedInUser, authConfig, singletonList(role));

        assertThat(assignedRoles, hasSize(1));
        assertThat(assignedRoles, contains("admin"));
    }

    @Test
    public void shouldNotAssignRoleIfUserIsNotMemberOfAnyGroup() throws Exception {
        final Role role = mock(Role.class);
        final KeycloakRoleConfiguration roleConfiguration = mock(KeycloakRoleConfiguration.class);

        when(role.name()).thenReturn("admin");
        when(role.roleConfiguration()).thenReturn(roleConfiguration);
        when(roleConfiguration.groups()).thenReturn(singletonList("group-1"));
        when(membershipChecker.isAMemberOfAtLeastOneGroup(loggedInUser, authConfig, roleConfiguration.groups())).thenReturn(false);

        final List<String> assignedRoles = authorizer.authorize(loggedInUser, authConfig, singletonList(role));

        assertThat(assignedRoles, hasSize(0));
    }
}
