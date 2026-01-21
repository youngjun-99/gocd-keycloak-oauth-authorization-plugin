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

import cd.go.authorization.keycloak.models.AuthConfig
import cd.go.authorization.keycloak.models.KeycloakRoleConfiguration
import cd.go.authorization.keycloak.models.Role
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KeycloakAuthorizerTest {

    private lateinit var authorizer: KeycloakAuthorizer

    @Mock
    private lateinit var loggedInUser: KeycloakUser

    @Mock
    private lateinit var membershipChecker: MembershipChecker

    @Mock
    private lateinit var authConfig: AuthConfig

    @BeforeEach
    fun setUp() {
        authorizer = KeycloakAuthorizer(membershipChecker)
    }

    @Test
    fun shouldReturnEmptyListIfNoRoleConfiguredForGivenAuthConfig() {
        val assignedRoles = authorizer.authorize(loggedInUser, authConfig, emptyList())

        assertThat(assignedRoles, hasSize(0))
        verifyNoInteractions(authConfig)
        verifyNoInteractions(membershipChecker)
    }

    @Test
    fun shouldAssignRoleIfUserIsAMemberOfAtLeastOneGroup() {
        val role = mock(Role::class.java)
        val roleConfiguration = mock(KeycloakRoleConfiguration::class.java)

        `when`(role.name()).thenReturn("admin")
        `when`(role.roleConfiguration()).thenReturn(roleConfiguration)
        `when`(roleConfiguration.groups()).thenReturn(listOf("group-1"))
        `when`(membershipChecker.isAMemberOfAtLeastOneGroup(loggedInUser, authConfig, roleConfiguration.groups())).thenReturn(true)

        val assignedRoles = authorizer.authorize(loggedInUser, authConfig, listOf(role))

        assertThat(assignedRoles, hasSize(1))
        assertThat(assignedRoles, contains("admin"))
    }

    @Test
    fun shouldNotAssignRoleIfUserIsNotMemberOfAnyGroup() {
        val role = mock(Role::class.java)
        val roleConfiguration = mock(KeycloakRoleConfiguration::class.java)

        `when`(role.name()).thenReturn("admin")
        `when`(role.roleConfiguration()).thenReturn(roleConfiguration)
        `when`(roleConfiguration.groups()).thenReturn(listOf("group-1"))
        `when`(membershipChecker.isAMemberOfAtLeastOneGroup(loggedInUser, authConfig, roleConfiguration.groups())).thenReturn(false)

        val assignedRoles = authorizer.authorize(loggedInUser, authConfig, listOf(role))

        assertThat(assignedRoles, hasSize(0))
    }
}
