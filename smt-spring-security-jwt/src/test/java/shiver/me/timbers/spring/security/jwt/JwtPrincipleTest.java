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
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JwtPrincipleTest {

    @Test
    public void Can_pack_a_jwt_principle() throws IOException {

        final MessagePack msgpack = new MessagePack();
        final String username = someString();
        final List<String> roles = asList(someString(), someString(), someString());

        // Given
        msgpack.register(JwtPrinciple.class);

        // When
        final JwtPrinciple actual = msgpack.read(msgpack.write(new JwtPrinciple(username, roles)), JwtPrinciple.class);

        // Then
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getRoles(), is(roles));
    }

    @Test
    public void Can_set_values() throws IOException {

        final JwtPrinciple principle = new JwtPrinciple();
        final String username = someString();
        final List roles = mock(List.class);

        // Given
        principle.setUsername(username);
        principle.setRoles(roles);

        // When
        final String actualUsername = principle.getUsername();
        final List<String> actualRoles = principle.getRoles();

        // Then
        assertThat(actualUsername, is(username));
        assertThat(actualRoles, is(roles));
    }
}