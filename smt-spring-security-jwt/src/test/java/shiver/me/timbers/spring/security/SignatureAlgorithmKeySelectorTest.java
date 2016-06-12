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

import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.security.InvalidKeyException;
import java.security.Key;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.SignatureAlgorithm.HS384;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.SignatureAlgorithm.NONE;
import static io.jsonwebtoken.SignatureAlgorithm.PS256;
import static io.jsonwebtoken.SignatureAlgorithm.PS384;
import static io.jsonwebtoken.SignatureAlgorithm.PS512;
import static io.jsonwebtoken.SignatureAlgorithm.RS256;
import static io.jsonwebtoken.SignatureAlgorithm.RS384;
import static io.jsonwebtoken.SignatureAlgorithm.RS512;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class SignatureAlgorithmKeySelectorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Base64Keys base64Keys;
    private RsaKeys rsaKeys;
    private KeySelector<SignatureAlgorithm> keySelector;

    @Before
    public void setUp() {
        base64Keys = mock(Base64Keys.class);
        rsaKeys = mock(RsaKeys.class);
        keySelector = new SignatureAlgorithmKeySelector(base64Keys, rsaKeys);
    }

    @Test
    public void Can_select_an_hmac_key() {

        final SignatureAlgorithm algorithm = someThing(NONE, HS256, HS384, HS512);
        final String secret = someString(5);

        final Key expected = mock(Key.class);

        // Given
        given(base64Keys.createKey(eq(algorithm), eq(secret.getBytes()))).willReturn(expected);

        // When
        final Key actual = keySelector.select(algorithm, secret);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_select_an_rsa_key() throws InvalidKeyException {

        final SignatureAlgorithm algorithm = someThing(RS256, RS384, RS512, PS256, PS384, PS512);
        final String secret = someString(5);

        final Key expected = mock(Key.class);

        // Given
        given(rsaKeys.createKey(secret)).willReturn(expected);

        // When
        final Key actual = keySelector.select(algorithm, secret);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_to_select_an_rsa_key() throws InvalidKeyException {

        final SignatureAlgorithm algorithm = someThing(RS256, RS384, RS512, PS256, PS384, PS512);
        final String secret = someString(5);

        final InvalidKeyException exception = new InvalidKeyException();

        // Given
        given(rsaKeys.createKey(secret)).willThrow(exception);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectCause(is(exception));

        // When
        keySelector.select(algorithm, secret);
    }
}