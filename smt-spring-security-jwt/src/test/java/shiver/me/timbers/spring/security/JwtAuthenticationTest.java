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

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JwtAuthenticationTest {

    private static final Set<GrantedAuthority> EMPTY_AUTHORITIES = Collections.<GrantedAuthority>emptySet();

    @Test
    @SuppressWarnings("unchecked")
    public void Can_get_the_principle() {

        // Given
        final String expected = someString();

        // When
        final Object actual = new JwtAuthentication(expected, EMPTY_AUTHORITIES).getPrincipal();

        // Then
        assertThat(actual, is((Object) expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_get_authorities() {

        final GrantedAuthority authority1 = mock(GrantedAuthority.class);
        final GrantedAuthority authority2 = mock(GrantedAuthority.class);
        final GrantedAuthority authority3 = mock(GrantedAuthority.class);

        // Given
        final Collection<GrantedAuthority> expected = asList(authority1, authority2, authority3);

        // When
        final Collection<GrantedAuthority> actual = new JwtAuthentication(someString(), expected).getAuthorities();

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Jwt_authentication_is_authenticated() {

        // When
        final boolean actual = new JwtAuthentication(someString(), EMPTY_AUTHORITIES).isAuthenticated();

        // Then
        assertThat(actual, is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Cannot_get_the_credentials() {

        // When
        final Object actual = new JwtAuthentication(someString(), EMPTY_AUTHORITIES).getCredentials();

        // Then
        assertThat(actual, nullValue());
    }
}