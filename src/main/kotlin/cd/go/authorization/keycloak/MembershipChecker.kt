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

import java.text.MessageFormat.format

class MembershipChecker {

    fun isAMemberOfAtLeastOneGroup(loggedInUser: KeycloakUser, groupsAllowed: List<String>): Boolean {
        if (groupsAllowed.isEmpty()) {
            KeycloakPlugin.LOG.info("[MembershipChecker] No groups provided.")
            return false
        }

        return checkMembershipUsingUsersAccessToken(loggedInUser, groupsAllowed)
    }

    private fun checkMembershipUsingUsersAccessToken(loggedInUser: KeycloakUser, groupsAllowed: List<String>): Boolean {
        val myGroups = loggedInUser.groups() ?: return false

        for (groupName in myGroups) {
            if (groupsAllowed.contains(groupName)) {
                KeycloakPlugin.LOG.debug(format("[MembershipChecker] User `{0}` is a member of `{1}` group.", loggedInUser.email, groupName))
                return true
            }
        }

        return false
    }
}
