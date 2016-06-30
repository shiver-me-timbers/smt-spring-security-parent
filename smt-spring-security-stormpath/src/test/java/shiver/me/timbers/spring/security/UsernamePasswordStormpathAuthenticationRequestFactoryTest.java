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

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.BasicAuthenticationOptions;
import com.stormpath.sdk.authc.UsernamePasswordRequestBuilder;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class UsernamePasswordStormpathAuthenticationRequestFactoryTest {

    @Test
    public void Can_create_a_username_and_password_stormpath_authentication_request() {

        final StormpathRequestBuilderFactory builderFactory = mock(StormpathRequestBuilderFactory.class);
        final String username = someString();
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);

        final String password = someString();
        final BasicAuthenticationOptions options = mock(BasicAuthenticationOptions.class);
        final BasicAuthenticationOptions optionsWithAccount = mock(BasicAuthenticationOptions.class);
        final UsernamePasswordRequestBuilder builder = mock(UsernamePasswordRequestBuilder.class);
        final UsernamePasswordRequestBuilder usernameBuilder = mock(UsernamePasswordRequestBuilder.class);
        final UsernamePasswordRequestBuilder passwordBuilder = mock(UsernamePasswordRequestBuilder.class);
        final UsernamePasswordRequestBuilder optionsBuilder = mock(UsernamePasswordRequestBuilder.class);

        final AuthenticationRequest expected = mock(AuthenticationRequest.class);

        // Given
        given(authentication.getCredentials()).willReturn(password);
        given(builderFactory.options()).willReturn(options);
        given(options.withAccount()).willReturn(optionsWithAccount);
        given(builderFactory.builder()).willReturn(builder);
        given(builder.setUsernameOrEmail(username)).willReturn(usernameBuilder);
        given(usernameBuilder.setPassword(password)).willReturn(passwordBuilder);
        given(passwordBuilder.withResponseOptions(optionsWithAccount)).willReturn(optionsBuilder);
        given(optionsBuilder.build()).willReturn(expected);

        // When
        final AuthenticationRequest actual = new UsernamePasswordStormpathAuthenticationRequestFactory(builderFactory)
            .create(username, authentication);

        // Then
        assertThat(actual, is(expected));
    }
}