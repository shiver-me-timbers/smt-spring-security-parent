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

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JJwtTokenParserTest {

    private Class<Object> type;
    private JwtEncryptor encryptor;
    private JwtDecryptor decryptor;
    private JwtTokenParser<Object, String> tokenParser;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        type = Object.class;
        encryptor = mock(JwtEncryptor.class);
        decryptor = mock(JwtDecryptor.class);
        tokenParser = new JJwtTokenParser<>(type, encryptor, decryptor);
    }

    @Test
    public void Can_create_a_jwt_token_from_a_principle() {

        final String principal = someString();

        final String expected = someString();

        // Given
        given(encryptor.encrypt(principal)).willReturn(expected);

        // When
        final String actual = tokenParser.create(principal);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_parse_a_jwt_token() throws IOException {

        final String token = someString();

        final Object expected = new Object();

        // Given
        given(decryptor.decrypt(token, type)).willReturn(expected);

        // When
        final Object actual = tokenParser.parse(token);

        // Then
        assertThat(actual, is(expected));
    }
}