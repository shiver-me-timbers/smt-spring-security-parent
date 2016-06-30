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
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class StormpathAuthenticationProviderTest {

    private Application application;
    private StormpathAuthenticationRequestFactory requests;
    private UserDetailsConverter converter;
    private StormpathAuthenticationProvider provider;

    @Before
    public void setUp() {
        application = mock(Application.class);
        requests = mock(StormpathAuthenticationRequestFactory.class);
        converter = mock(UserDetailsConverter.class);
        provider = new StormpathAuthenticationProvider(application, requests, converter);
    }

    @Test
    public void Can_verify_that_the_accounts_password_is_correct() {

        final UserDetails userDetails = mock(UserDetails.class);
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);

        final String password = someString();

        // Given
        given(userDetails.getPassword()).willReturn(password);
        given(authentication.getCredentials()).willReturn(password);

        // When
        provider.additionalAuthenticationChecks(userDetails, authentication);

        // Then
        verify(userDetails).getPassword();
        verify(authentication).getCredentials();
    }

    @Test(expected = BadCredentialsException.class)
    public void Can_verify_that_the_accounts_password_is_in_correct() {

        final UserDetails userDetails = mock(UserDetails.class);
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);

        // Given
        given(userDetails.getPassword()).willReturn(someString());
        given(authentication.getCredentials()).willReturn(someString());

        // When
        provider.additionalAuthenticationChecks(userDetails, authentication);
    }

    @Test
    public void Can_retrieve_the_user_details() {

        final String username = someString();
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);

        final AuthenticationRequest request = mock(AuthenticationRequest.class);
        final AuthenticationResult result = mock(AuthenticationResult.class);
        final UserDetails expected = mock(UserDetails.class);

        // Given
        given(requests.create(username, authentication)).willReturn(request);
        given(application.authenticateAccount(request)).willReturn(result);
        given(converter.convert(result)).willReturn(expected);

        // When
        final UserDetails actual = provider.retrieveUser(username, authentication);

        // Then
        assertThat(actual, is(expected));
    }
}