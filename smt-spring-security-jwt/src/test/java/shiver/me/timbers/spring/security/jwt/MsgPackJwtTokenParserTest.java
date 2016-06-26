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
import org.msgpack.MessagePack;
import shiver.me.timbers.spring.security.Base64;
import shiver.me.timbers.spring.security.time.Clock;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static shiver.me.timbers.data.random.RandomBytes.someBytes;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.somePositiveInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class MsgPackJwtTokenParserTest {

    private static final String PRINCIPAL = "principal";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private JwtParser parser;
    private JwtBuilder builder;
    private SignatureAlgorithm algorithm;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private KeyPair keyPair;
    private Integer expiryDuration;
    private TimeUnit expiryUnit;
    private Clock clock;
    private MessagePack messagePack;
    private Base64 base64;
    private JwtTokenParser<Object, String> tokenParser;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        parser = mock(JwtParser.class);
        builder = mock(JwtBuilder.class);
        algorithm = someEnum(SignatureAlgorithm.class);
        publicKey = mock(PublicKey.class);
        privateKey = mock(PrivateKey.class);
        keyPair = new KeyPair(publicKey, privateKey);
        expiryDuration = somePositiveInteger();
        expiryUnit = someEnum(TimeUnit.class);
        clock = mock(Clock.class);
        messagePack = mock(MessagePack.class);
        base64 = mock(Base64.class);
        tokenParser = new MsgPackJwtTokenParser<>(
            Object.class,
            builder,
            parser,
            algorithm,
            keyPair,
            expiryDuration,
            expiryUnit,
            clock,
            messagePack,
            base64
        );
    }

    @Test
    public void Can_create_a_jwt_token_from_a_principle() throws IOException {

        final String principal = someString();

        final byte[] bytes = new byte[0];
        final String packedPrinciple = someString();
        final JwtBuilder principleBuilder = mock(JwtBuilder.class);
        final JwtBuilder secretBuilder = mock(JwtBuilder.class);
        final Date date = mock(Date.class);
        final JwtBuilder expiringBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(messagePack.write(principal)).willReturn(bytes);
        given(base64.encode(bytes)).willReturn(packedPrinciple);
        given(builder.claim(PRINCIPAL, packedPrinciple)).willReturn(principleBuilder);
        given(principleBuilder.signWith(algorithm, privateKey)).willReturn(secretBuilder);
        given(clock.nowPlus(expiryDuration, expiryUnit)).willReturn(date);
        given(secretBuilder.setExpiration(date)).willReturn(expiringBuilder);
        given(expiringBuilder.compact()).willReturn(expected);

        // When
        final String actual = tokenParser.create(principal);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_create_a_jwt_token_with_no_expiry() throws IOException {

        final String principal = someString();

        final byte[] bytes = someBytes();
        final String packedPrinciple = someString();
        final JwtBuilder principleBuilder = mock(JwtBuilder.class);
        final JwtBuilder secretBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(messagePack.write(principal)).willReturn(bytes);
        given(base64.encode(bytes)).willReturn(packedPrinciple);
        given(builder.claim(PRINCIPAL, packedPrinciple)).willReturn(principleBuilder);
        given(principleBuilder.signWith(algorithm, privateKey)).willReturn(secretBuilder);
        given(secretBuilder.compact()).willReturn(expected);

        // When
        final String actual = new MsgPackJwtTokenParser<>(
            Object.class, builder, parser, algorithm, keyPair, -1, expiryUnit, clock,
            messagePack, base64).create(principal);

        // Then
        verifyZeroInteractions(clock);
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_to_pack_a_jwt_token() throws IOException {

        final String principal = someString();

        final IOException exception = new IOException();

        // Given
        given(messagePack.write(principal)).willThrow(exception);
        expectedException.expect(JwtInvalidTokenException.class);
        expectedException.expectCause(is(exception));

        // When
        tokenParser.create(principal);
    }

    @Test
    public void Can_parse_a_jwt_token() throws IOException {

        final String token = someString();

        final JwtParser secretParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims claims = mock(Claims.class);
        final String principal = someString();
        final byte[] bytes = someBytes();

        final Object expected = new Object();

        // Given
        given(parser.setSigningKey(publicKey)).willReturn(secretParser);
        given(secretParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(claims);
        given(claims.get(PRINCIPAL)).willReturn(principal);
        given(base64.decode(principal)).willReturn(bytes);
        given(messagePack.<Object>read(bytes, Object.class)).willReturn(expected);

        // When
        final Object actual = tokenParser.parse(token);

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
        tokenParser.parse(token);
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
        tokenParser.parse("");
    }

    @Test
    public void Can_fail_unpack_a_jwt_token() throws IOException {

        final String token = someString();

        final JwtParser secretParser = mock(JwtParser.class);
        @SuppressWarnings("unchecked")
        final Jws<Claims> jws = mock(Jws.class);
        final Claims claims = mock(Claims.class);
        final String principal = someString();
        final byte[] bytes = someBytes();
        final IOException exception = new IOException();

        // Given
        given(parser.setSigningKey(publicKey)).willReturn(secretParser);
        given(secretParser.parseClaimsJws(token)).willReturn(jws);
        given(jws.getBody()).willReturn(claims);
        given(claims.get(PRINCIPAL)).willReturn(principal);
        given(base64.decode(principal)).willReturn(bytes);
        given(messagePack.<Object>read(bytes, Object.class)).willThrow(exception);
        expectedException.expect(JwtInvalidTokenException.class);
        expectedException.expectCause(is(exception));

        // When
        tokenParser.parse(token);
    }
}