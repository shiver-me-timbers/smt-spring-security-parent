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
import com.stormpath.sdk.authc.AuthenticationResult;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static com.stormpath.sdk.account.AccountStatus.ENABLED;

/**
 * @author Karl Bennett
 */
public class StormpathUserDetailsFactory implements UserDetailsFactory {

    private final GroupGrantedAuthorityConverter authorityConverter;

    public StormpathUserDetailsFactory(GroupGrantedAuthorityConverter authorityConverter) {
        this.authorityConverter = authorityConverter;
    }

    @Override
    public AccountUserDetails create(AuthenticationResult result) {
        final Account account = result.getAccount();
        final Collection<? extends GrantedAuthority> authorities = authorityConverter.convert(account.getGroups());
        return new StormpathAccountUserDetails(authorities, account);
    }

    private static class StormpathAccountUserDetails implements AccountUserDetails {

        private final Collection<? extends GrantedAuthority> authorities;
        private final Account account;

        public StormpathAccountUserDetails(Collection<? extends GrantedAuthority> authorities, Account account) {
            this.authorities = authorities;
            this.account = account;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public String getUsername() {
            return account.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return ENABLED.equals(account.getStatus());
        }

        @Override
        public Account getAccount() {
            return account;
        }
    }
}
