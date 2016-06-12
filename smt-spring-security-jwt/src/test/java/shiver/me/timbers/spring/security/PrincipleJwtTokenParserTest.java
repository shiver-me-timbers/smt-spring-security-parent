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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class PrincipleJwtTokenParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private String secret;
    private JwtParser parser;
    private PrincipleJwtTokenParser tokenParser;

    @Before
    public void setUp() {
        secret = someString();
        parser = mock(JwtParser.class);
        tokenParser = new PrincipleJwtTokenParser(secret, parser);
    }

    @Test
    public void Can_parse_a_jwt_token() throws JwtInvalidTokenException {

        final String token = someString();

        final JwtParser secretParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims claims = mock(Claims.class);

        final String expected = someString();

        // Given
        given(parser.setSigningKey(secret)).willReturn(secretParser);
        given(secretParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(claims);
        given(claims.get("principle")).willReturn(expected);

        // When
        final String actual = tokenParser.parse(token);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_parse_a_jwt_token() throws JwtInvalidTokenException {

        final String token = someString();

        final JwtParser secretParser = mock(JwtParser.class);

        final JwtException exception = new JwtException(someString());

        // Given
        given(parser.setSigningKey(secret)).willReturn(secretParser);
        given(secretParser.parseClaimsJws(token)).willThrow(exception);
        expectedException.expect(JwtInvalidTokenException.class);
        expectedException.expectCause(is(exception));

        // When
        tokenParser.parse(token);
    }

    @Test
    public void Can_fail_to_parse_an_empty_jwt_token() throws JwtInvalidTokenException {

        final JwtParser secretParser = mock(JwtParser.class);

        final IllegalArgumentException exception = new IllegalArgumentException();

        // Given
        given(parser.setSigningKey(secret)).willReturn(secretParser);
        given(secretParser.parseClaimsJws("")).willThrow(exception);
        expectedException.expect(JwtInvalidTokenException.class);
        expectedException.expectCause(is(exception));

        // When
        tokenParser.parse("");
    }
}