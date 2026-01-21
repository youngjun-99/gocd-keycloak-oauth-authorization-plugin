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
import cd.go.authorization.keycloak.models.Role
import java.text.MessageFormat.format

class KeycloakAuthorizer(
    private val membershipChecker: MembershipChecker
) {
    constructor() : this(MembershipChecker())

    fun authorize(loggedInUser: KeycloakUser, authConfig: AuthConfig, roles: List<Role>): List<String> {
        val assignedRoles = mutableListOf<String>()

        if (roles.isEmpty()) {
            return assignedRoles
        }

        KeycloakPlugin.LOG.info(format("[Authorize] Authorizing user {0}", loggedInUser.email))

        for (role in roles) {
            val allowedUsers = role.roleConfiguration()?.users() ?: emptyList()
            if (allowedUsers.isNotEmpty() && allowedUsers.contains(loggedInUser.email?.lowercase())) {
                KeycloakPlugin.LOG.debug(format("[Authorize] Assigning role `{0}` to user `{1}`. As user belongs to allowed users list.", role.name(), loggedInUser.email))
                role.name()?.let { assignedRoles.add(it) }
                continue
            }

            val groups = role.roleConfiguration()?.groups() ?: emptyList()
            if (membershipChecker.isAMemberOfAtLeastOneGroup(loggedInUser, authConfig, groups)) {
                KeycloakPlugin.LOG.debug(format("[Authorize] Assigning role `{0}` to user `{1}`. As user is a member of at least one group.", role.name(), loggedInUser.email))
                role.name()?.let { assignedRoles.add(it) }
            }
        }

        KeycloakPlugin.LOG.debug(format("[Authorize] User `{0}` is authorized with `{1}` role(s).", loggedInUser.email, assignedRoles))

        return assignedRoles
    }
}
