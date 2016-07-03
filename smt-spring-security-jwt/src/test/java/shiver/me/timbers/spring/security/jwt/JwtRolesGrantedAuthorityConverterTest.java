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

package shiver.me.timbers.spring.security.jwt;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.matchers.Matchers.hasField;

public class JwtRolesGrantedAuthorityConverterTest {

    private RolesGrantedAuthorityConverter converter;

    @Before
    public void setUp() {
        converter = new JwtRolesGrantedAuthorityConverter();
    }

    @Test
    public void Can_convert_some_roles_to_some_granted_authorities() {

        // Given
        final String role1 = someString();
        final String role2 = someString();
        final String role3 = someString();

        // When
        final Collection<? extends GrantedAuthority> actual = converter.convert(asList(role1, role2, role3));

        // Then
        assertThat(actual, contains(hasField("role", role1), hasField("role", role2), hasField("role", role3)));
    }

    @Test
    public void Can_convert_null_roles_to_empty_granted_authorities() {

        // When
        final Collection<? extends GrantedAuthority> actual = converter.convert((List<String>) null);

        // Then
        assertThat(actual, empty());
    }

    @Test
    public void Can_convert_some_granted_authorities_into_roles() {

        final GrantedAuthority grantedAuthority1 = mock(GrantedAuthority.class);
        final GrantedAuthority grantedAuthority2 = mock(GrantedAuthority.class);
        final GrantedAuthority grantedAuthority3 = mock(GrantedAuthority.class);
        final String role1 = someString();
        final String role2 = someString();
        final String role3 = someString();

        // Given
        given(grantedAuthority1.getAuthority()).willReturn(role1);
        given(grantedAuthority2.getAuthority()).willReturn(role2);
        given(grantedAuthority3.getAuthority()).willReturn(role3);

        // When
        final List<String> actual = converter.convert(asList(grantedAuthority1, grantedAuthority2, grantedAuthority3));

        // Then
        assertThat(actual, contains(role1, role2, role3));
    }

    @Test
    public void Can_convert_null_granted_authorities_empty_roles() {

        // When
        final List<String> actual = converter.convert((Collection<? extends GrantedAuthority>) null);

        // Then
        assertThat(actual, empty());
    }
}