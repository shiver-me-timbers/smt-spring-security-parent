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

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import shiver.me.timbers.spring.security.time.Clock;

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
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.somePositiveInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class JJwtEncryptorTest {

    private static final String PRINCIPAL = "principal";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private JwtBuilder builder;
    private SignatureAlgorithm algorithm;
    private PrivateKey privateKey;
    private KeyPair keyPair;
    private Integer expiryDuration;
    private TimeUnit expiryUnit;
    private Clock clock;
    private JwtEncryptor encryptor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        builder = mock(JwtBuilder.class);
        algorithm = someEnum(SignatureAlgorithm.class);
        privateKey = mock(PrivateKey.class);
        keyPair = new KeyPair(mock(PublicKey.class), privateKey);
        expiryDuration = somePositiveInteger();
        expiryUnit = someEnum(TimeUnit.class);
        clock = mock(Clock.class);
        encryptor = new JJwtEncryptor(builder, algorithm, keyPair, expiryDuration, expiryUnit, clock);
    }

    @Test
    public void Can_create_a_jwt_token_from_a_principle() {

        final String principal = someString();

        final JwtBuilder principleBuilder = mock(JwtBuilder.class);
        final JwtBuilder secretBuilder = mock(JwtBuilder.class);
        final Date date = mock(Date.class);
        final JwtBuilder expiringBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(builder.claim(PRINCIPAL, principal)).willReturn(principleBuilder);
        given(principleBuilder.signWith(algorithm, privateKey)).willReturn(secretBuilder);
        given(clock.nowPlus(expiryDuration, expiryUnit)).willReturn(date);
        given(secretBuilder.setExpiration(date)).willReturn(expiringBuilder);
        given(expiringBuilder.compact()).willReturn(expected);

        // When
        final String actual = encryptor.encrypt(principal);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_create_a_jwt_token_with_no_expiry() {

        final String principal = someString();

        final JwtBuilder principleBuilder = mock(JwtBuilder.class);
        final JwtBuilder secretBuilder = mock(JwtBuilder.class);

        final String expected = someString();

        // Given
        given(builder.claim(PRINCIPAL, principal)).willReturn(principleBuilder);
        given(principleBuilder.signWith(algorithm, privateKey)).willReturn(secretBuilder);
        given(secretBuilder.compact()).willReturn(expected);

        // When
        final String actual = new JJwtEncryptor(builder, algorithm, keyPair, -1, expiryUnit, clock)
            .encrypt(principal);

        // Then
        verifyZeroInteractions(clock);
        assertThat(actual, is(expected));
    }
}