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

import com.stormpath.sdk.application.Application;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Karl Bennett
 */
public class StormpathAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final Application application;
    private final StormpathAuthenticationRequestFactory requests;
    private final UserDetailsConverter converter;

    public StormpathAuthenticationProvider(
        Application application,
        StormpathAuthenticationRequestFactory requests,
        UserDetailsConverter converter
    ) {
        this.application = application;
        this.requests = requests;
        this.converter = converter;
    }

    @Override
    protected void additionalAuthenticationChecks(
        UserDetails userDetails,
        UsernamePasswordAuthenticationToken authentication
    ) throws AuthenticationException {
        if (!userDetails.getPassword().equals(authentication.getCredentials())) {
            throw new BadCredentialsException("Password is incorrect.");
        }
    }

    @Override
    protected UserDetails retrieveUser(
        String username,
        UsernamePasswordAuthenticationToken authentication
    ) throws AuthenticationException {
        return converter.convert(application.authenticateAccount(requests.create(username, authentication)));
    }
}
