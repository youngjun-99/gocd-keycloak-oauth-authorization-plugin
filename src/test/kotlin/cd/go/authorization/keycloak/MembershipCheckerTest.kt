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

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MembershipCheckerTest {

    private lateinit var membershipChecker: MembershipChecker

    @BeforeEach
    fun setUp() {
        membershipChecker = MembershipChecker()
    }

    @Test
    fun shouldReturnFalseWhenNoGroupsAllowed() {
        val user = KeycloakUser.fromJSON("""{"email":"user@test.com","groups":["/admins"]}""")

        assertFalse(membershipChecker.isAMemberOfAtLeastOneGroup(user, emptyList()))
    }

    @Test
    fun shouldReturnFalseWhenUserHasNoGroups() {
        val user = KeycloakUser.fromJSON("""{"email":"user@test.com"}""")

        assertFalse(membershipChecker.isAMemberOfAtLeastOneGroup(user, listOf("/admins")))
    }

    @Test
    fun shouldReturnTrueWhenUserBelongsToAllowedGroup() {
        val user = KeycloakUser.fromJSON("""{"email":"user@test.com","groups":["/admins","/developers"]}""")

        assertTrue(membershipChecker.isAMemberOfAtLeastOneGroup(user, listOf("/admins")))
    }

    @Test
    fun shouldReturnFalseWhenUserDoesNotBelongToAnyAllowedGroup() {
        val user = KeycloakUser.fromJSON("""{"email":"user@test.com","groups":["/developers"]}""")

        assertFalse(membershipChecker.isAMemberOfAtLeastOneGroup(user, listOf("/admins", "/managers")))
    }

    @Test
    fun shouldReturnTrueWhenUserBelongsToAtLeastOneAllowedGroup() {
        val user = KeycloakUser.fromJSON("""{"email":"user@test.com","groups":["/developers","/admins"]}""")

        assertTrue(membershipChecker.isAMemberOfAtLeastOneGroup(user, listOf("/admins", "/managers")))
    }

    @Test
    fun shouldReturnFalseWhenUserGroupsIsNull() {
        val user = KeycloakUser("user@test.com", "Test User")

        assertFalse(membershipChecker.isAMemberOfAtLeastOneGroup(user, listOf("/admins")))
    }
}
