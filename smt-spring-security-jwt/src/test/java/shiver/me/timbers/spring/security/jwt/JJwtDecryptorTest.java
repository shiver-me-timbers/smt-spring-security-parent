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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JJwtDecryptorTest {

    private static final String PRINCIPAL = "principal";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private JwtParser parser;
    private PublicKey publicKey;
    private KeyPair keyPair;
    private ObjectMapper objectMapper;
    private JwtDecryptor decryptor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        parser = mock(JwtParser.class);
        publicKey = mock(PublicKey.class);
        keyPair = new KeyPair(publicKey, mock(PrivateKey.class));
        objectMapper = mock(ObjectMapper.class);
        decryptor = new JJwtDecryptor(parser, keyPair, objectMapper);
    }

    @Test
    public void Can_parse_a_jwt_token() throws IOException {

        final String token = someString();
        final Class<Object> type = Object.class;

        final JwtParser secretParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims claims = mock(Claims.class);
        final Map map = mock(Map.class);

        final Object expected = new Object();

        // Given
        given(parser.setSigningKey(publicKey)).willReturn(secretParser);
        given(secretParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(claims);
        given(claims.get(PRINCIPAL, Map.class)).willReturn(map);
        given(objectMapper.convertValue(map, type)).willReturn(expected);

        // When
        final Object actual = decryptor.decrypt(token, type);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_parse_a_jwt_token() throws JwtInvalidTokenException {

        final String token = someString();

        final JwtParser secretParser = mock(JwtParser.class);

        final JwtException exception = new JwtException(someString());

        // Given
        given(parser.setSigningKey(publicKey)).willReturn(secretParser);
        given(secretParser.parseClaimsJws(token)).willThrow(exception);
        expectedException.expect(JwtInvalidTokenException.class);
        expectedException.expectCause(is(exception));

        // When
        decryptor.decrypt(token, Object.class);
    }

    @Test
    public void Can_fail_to_parse_an_empty_jwt_token() throws JwtInvalidTokenException {

        final JwtParser secretParser = mock(JwtParser.class);

        final IllegalArgumentException exception = new IllegalArgumentException();

        // Given
        given(parser.setSigningKey(publicKey)).willReturn(secretParser);
        given(secretParser.parseClaimsJws("")).willThrow(exception);
        expectedException.expect(JwtInvalidTokenException.class);
        expectedException.expectCause(is(exception));

        // When
        decryptor.decrypt("", Object.class);
    }
}