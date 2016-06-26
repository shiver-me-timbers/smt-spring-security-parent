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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JwtPrincipalAuthenticationConverterTest {

    private GrantedAuthorityConverter<List<String>> grantedAuthorityConverter;
    private AuthenticationConverter<JwtPrincipal> converter;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        grantedAuthorityConverter = mock(GrantedAuthorityConverter.class);
        converter = new JwtPrincipalAuthenticationConverter(grantedAuthorityConverter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_convert_an_authentication_to_a_jwt_principle() {

        final Authentication authentication = mock(Authentication.class);

        final UserDetails userDetails = mock(UserDetails.class);
        final String username = someString();
        final Collection<GrantedAuthority> authorities = mock(Collection.class);
        final List<String> roles = mock(List.class);

        // Given
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(userDetails.getUsername()).willReturn(username);
        given(authentication.getAuthorities()).willReturn((Collection) authorities);
        given(grantedAuthorityConverter.convert(authorities)).willReturn(roles);

        // When
        final JwtPrincipal actual = converter.convert(authentication);

        // Then
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getRoles(), is(roles));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_convert_a_jwt_principle_to_a_jwt_authentication() {

        final JwtPrincipal principal = mock(JwtPrincipal.class);

        final List<String> roles = mock(List.class);
        final String username = someString();
        final Collection<GrantedAuthority> authorities = asList(
            mock(GrantedAuthority.class), mock(GrantedAuthority.class), mock(GrantedAuthority.class)
        );

        // Given
        given(principal.getUsername()).willReturn(username);
        given(principal.getRoles()).willReturn(roles);
        given(grantedAuthorityConverter.convert(roles)).willReturn((Collection) authorities);

        // When
        final Authentication actual = converter.convert(principal);

        // Then
        assertThat(actual.getPrincipal(), is((Object) username));
        assertThat(actual.getAuthorities(), is((Collection) authorities));
        assertThat(actual.isAuthenticated(), is(true));
    }
}