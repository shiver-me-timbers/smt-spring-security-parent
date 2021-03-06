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

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JwtPrincipalTest {

    @Test
    public void Can_set_values() throws IOException {

        // Given
        final String username = someString();
        @SuppressWarnings("unchecked")
        final List<String> roles = mock(List.class);
        final JwtPrincipal principal = new JwtPrincipal(username, roles);

        // When
        final String actualUsername = principal.getUsername();
        final List<String> actualRoles = principal.getRoles();

        // Then
        assertThat(actualUsername, is(username));
        assertThat(actualRoles, is(roles));
    }
}