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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.group.GroupList;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.EnumSet;

import static com.stormpath.sdk.account.AccountStatus.ENABLED;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class StormpathUserDetailsConverterTest {

    private GroupGrantedAuthorityConverter authorityConverter;
    private StormpathUserDetailsConverter converter;

    @Before
    public void setUp() {
        authorityConverter = mock(GroupGrantedAuthorityConverter.class);
        converter = new StormpathUserDetailsConverter(authorityConverter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_convert_an_authentication_into_user_details() {

        final AuthenticationResult result = mock(AuthenticationResult.class);

        final Account account = mock(Account.class);
        final String username = someString();
        final GroupList groups = mock(GroupList.class);
        final Collection authorities = mock(Collection.class);

        // Given
        given(result.getAccount()).willReturn(account);
        given(account.getUsername()).willReturn(username);
        given(account.getStatus()).willReturn(ENABLED);
        given(account.getGroups()).willReturn(groups);
        given(authorityConverter.convert(groups)).willReturn(authorities);

        // When
        final UserDetails actual = converter.convert(result);

        // Then
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getPassword(), nullValue());
        assertThat(actual.getAuthorities(), is(authorities));
        assertThat(actual.isAccountNonExpired(), is(true));
        assertThat(actual.isAccountNonLocked(), is(true));
        assertThat(actual.isCredentialsNonExpired(), is(true));
        assertThat(actual.isEnabled(), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_convert_a_disabled_authentication_into_user_details() {

        final AuthenticationResult result = mock(AuthenticationResult.class);

        final Account account = mock(Account.class);
        final String username = someString();
        final EnumSet<AccountStatus> statuses = EnumSet.allOf(AccountStatus.class);
        final GroupList groups = mock(GroupList.class);
        final Collection authorities = mock(Collection.class);

        // Given
        given(result.getAccount()).willReturn(account);
        given(account.getUsername()).willReturn(username);
        statuses.remove(ENABLED);
        given(account.getStatus()).willReturn(someThing(statuses.toArray(new AccountStatus[statuses.size()])));
        given(account.getGroups()).willReturn(groups);
        given(authorityConverter.convert(groups)).willReturn(authorities);

        // When
        final UserDetails actual = converter.convert(result);

        // Then
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getPassword(), nullValue());
        assertThat(actual.getAuthorities(), is(authorities));
        assertThat(actual.isAccountNonExpired(), is(true));
        assertThat(actual.isAccountNonLocked(), is(true));
        assertThat(actual.isCredentialsNonExpired(), is(true));
        assertThat(actual.isEnabled(), is(false));
    }
}