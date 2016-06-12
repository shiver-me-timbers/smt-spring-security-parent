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
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.somePositiveInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class PrincipleJwtTokenParserTest {

    private static final String PRINCIPLE = "principle";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private String secret;
    private JwtParser parser;
    private JwtBuilder builder;
    private SignatureAlgorithm tokenHashing;
    private KeySelector<SignatureAlgorithm> keySelector;
    private Integer expiryDuration;
    private TimeUnit expiryUnit;
    private Clock clock;
    private JwtTokenParser<String, String> tokenParser;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        secret = someString();
        parser = mock(JwtParser.class);
        builder = mock(JwtBuilder.class);
        tokenHashing = someEnum(SignatureAlgorithm.class);
        keySelector = mock(KeySelector.class);
        expiryDuration = somePositiveInteger();
        expiryUnit = someEnum(TimeUnit.class);
        clock = mock(Clock.class);
        tokenParser = new PrincipleJwtTokenParser(
            secret,
            builder,
            parser,
            tokenHashing,
            keySelector,
            expiryDuration,
            expiryUnit,
            clock
        );
    }

    @Test
    public void Can_create_a_jwt_token_from_a_principle() throws JwtInvalidTokenException {

        final String principle = someString();

        final Key key = mock(Key.class);
        final Date date = mock(Date.class);
        final JwtBuilder principleBuilder = mock(JwtBuilder.class);
        final JwtBuilder expiringBuilder = mock(JwtBuilder.class);
        final JwtBuilder secretBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(builder.claim(PRINCIPLE, principle)).willReturn(principleBuilder);
        given(keySelector.select(tokenHashing, secret)).willReturn(key);
        given(principleBuilder.signWith(tokenHashing, key)).willReturn(secretBuilder);
        given(clock.nowPlus(expiryDuration, expiryUnit)).willReturn(date);
        given(secretBuilder.setExpiration(date)).willReturn(expiringBuilder);
        given(expiringBuilder.compact()).willReturn(expected);

        // When
        final String actual = tokenParser.create(principle);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_create_a_jwt_token_with_no_expiry() throws JwtInvalidTokenException {

        final String principle = someString();

        final Key key = mock(Key.class);
        final JwtBuilder principleBuilder = mock(JwtBuilder.class);
        final JwtBuilder secretBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(builder.claim(PRINCIPLE, principle)).willReturn(principleBuilder);
        given(keySelector.select(tokenHashing, secret)).willReturn(key);
        given(principleBuilder.signWith(tokenHashing, key)).willReturn(secretBuilder);
        given(secretBuilder.compact()).willReturn(expected);

        // When
        final String actual = new PrincipleJwtTokenParser(secret, builder, parser, tokenHashing, keySelector, -1, expiryUnit, clock)
            .create(principle);

        // Then
        verifyZeroInteractions(clock);
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_parse_a_jwt_token() throws JwtInvalidTokenException {

        final String token = someString();

        final Key key = mock(Key.class);
        final JwtParser secretParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims claims = mock(Claims.class);

        final String expected = someString();

        // Given
        given(keySelector.select(tokenHashing, secret)).willReturn(key);
        given(parser.setSigningKey(key)).willReturn(secretParser);
        given(secretParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(claims);
        given(claims.get(PRINCIPLE)).willReturn(expected);

        // When
        final String actual = tokenParser.parse(token);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_parse_a_jwt_token() throws JwtInvalidTokenException {

        final String token = someString();

        final Key key = mock(Key.class);
        final JwtParser secretParser = mock(JwtParser.class);

        final JwtException exception = new JwtException(someString());

        // Given
        given(keySelector.select(tokenHashing, secret)).willReturn(key);
        given(parser.setSigningKey(key)).willReturn(secretParser);
        given(secretParser.parseClaimsJws(token)).willThrow(exception);
        expectedException.expect(JwtInvalidTokenException.class);
        expectedException.expectCause(is(exception));

        // When
        tokenParser.parse(token);
    }

    @Test
    public void Can_fail_to_parse_an_empty_jwt_token() throws JwtInvalidTokenException {

        final JwtParser secretParser = mock(JwtParser.class);

        final Key key = mock(Key.class);
        final IllegalArgumentException exception = new IllegalArgumentException();

        // Given
        given(keySelector.select(tokenHashing, secret)).willReturn(key);
        given(parser.setSigningKey(key)).willReturn(secretParser);
        given(secretParser.parseClaimsJws("")).willThrow(exception);
        expectedException.expect(JwtInvalidTokenException.class);
        expectedException.expectCause(is(exception));

        // When
        tokenParser.parse("");
    }
}