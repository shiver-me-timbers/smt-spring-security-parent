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

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JwtPrincipalMapConverterTest {

    @Test
    public void Can_convert_a_map_into_a_jwt_principal() {

        final HashMap map = new HashMap();

        final String username = someString();
        final List<String> roles = asList(someString(), someString(), someString());

        // Given
        map.put("username", username);
        map.put("roles", roles);

        // When
        final JwtPrincipal actual = new JwtPrincipalMapConverter().convert(map);

        // Then
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getRoles(), is(roles));
    }
}