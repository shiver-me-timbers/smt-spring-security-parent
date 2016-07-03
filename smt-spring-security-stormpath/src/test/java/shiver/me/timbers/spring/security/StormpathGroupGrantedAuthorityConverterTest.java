/*
 * Copyright 2016 Karl Bennett
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

package shiver.me.timbers.spring.security;

import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static com.stormpath.sdk.group.GroupStatus.DISABLED;
import static com.stormpath.sdk.group.GroupStatus.ENABLED;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.matchers.Matchers.hasField;

public class StormpathGroupGrantedAuthorityConverterTest {

    @Test
    public void Can_convert_groups_to_granted_authorities() {

        final GroupList groups = mock(GroupList.class);

        final Group group1 = mock(Group.class);
        final Group group2 = mock(Group.class);
        final Group group3 = mock(Group.class);
        final String groupName1 = someString();
        final String groupName3 = someString();

        // Given
        given(groups.iterator()).willReturn(asList(group1, group2, group3).iterator());
        given(group1.getStatus()).willReturn(ENABLED);
        given(group1.getName()).willReturn(groupName1);
        given(group2.getStatus()).willReturn(DISABLED);
        given(group2.getName()).willReturn(someString());
        given(group3.getStatus()).willReturn(ENABLED);
        given(group3.getName()).willReturn(groupName3);

        // When
        final Collection<? extends GrantedAuthority> actual = new StormpathGroupGrantedAuthorityConverter().convert(groups);

        // Then
        assertThat(actual, contains(hasField("role", groupName1), hasField("role", groupName3)));
    }
}