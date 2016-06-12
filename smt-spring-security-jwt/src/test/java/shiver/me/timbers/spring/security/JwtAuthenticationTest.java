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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JwtAuthenticationTest {

    @Test
    public void Can_get_the_principle() {

        // Given
        final String expected = someString();

        // When
        final Object actual = new JwtAuthentication(expected).getPrincipal();

        // Then
        assertThat(actual, is((Object) expected));
    }

    @Test
    public void Jwt_authentication_is_authenticated() {

        // When
        final boolean actual = new JwtAuthentication(someString()).isAuthenticated();

        // Then
        assertThat(actual, is(true));
    }

    @Test
    public void Cannot_get_the_credentials() {

        // When
        final Object actual = new JwtAuthentication(someString()).getCredentials();

        // Then
        assertThat(actual, nullValue());
    }
}