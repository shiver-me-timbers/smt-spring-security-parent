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

package shiver.me.timbers.spring.security.keys;

import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static io.jsonwebtoken.SignatureAlgorithm.ES256;
import static io.jsonwebtoken.SignatureAlgorithm.ES384;
import static io.jsonwebtoken.SignatureAlgorithm.ES512;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class SignatureAlgorithmKeyParserTest {

    private Base64KeyPairs base64KeyPairs;
    private PemKeyPairs pemKeyPairs;

    @Before
    public void setUp() {
        base64KeyPairs = mock(Base64KeyPairs.class);
        pemKeyPairs = mock(PemKeyPairs.class);
    }

    @Test
    public void Can_select_an_hmac_key() throws IOException {

        final SignatureAlgorithm algorithm = someThing(NONE, HS256, HS384, HS512);
        final String secret = someString(5);

        final KeyPair expected = new KeyPair(mock(PublicKey.class), mock(PrivateKey.class));

        // Given
        given(base64KeyPairs.createPair(secret)).willReturn(expected);

        // When
        final KeyPair actual = new SignatureAlgorithmKeyParser(algorithm, base64KeyPairs, pemKeyPairs)
            .parse(secret);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_select_an_rsa_key() throws IOException {

        final SignatureAlgorithm algorithm = someThing(RS256, RS384, RS512, PS256, PS384, PS512);
        final String secret = someString(5);

        final KeyPair expected = new KeyPair(mock(PublicKey.class), mock(PrivateKey.class));

        // Given
        given(pemKeyPairs.createPair(secret)).willReturn(expected);

        // When
        final KeyPair actual = new SignatureAlgorithmKeyParser(algorithm, base64KeyPairs, pemKeyPairs)
            .parse(secret);

        // Then
        verifyZeroInteractions(base64KeyPairs);
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_select_an_dsa_key() throws IOException {

        final SignatureAlgorithm algorithm = someThing(ES256, ES384, ES512);
        final String secret = someString(5);

        final KeyPair expected = new KeyPair(mock(PublicKey.class), mock(PrivateKey.class));

        // Given
        given(pemKeyPairs.createPair(secret)).willReturn(expected);

        // When
        final KeyPair actual = new SignatureAlgorithmKeyParser(algorithm, base64KeyPairs, pemKeyPairs)
            .parse(secret);

        // Then
        verifyZeroInteractions(base64KeyPairs);
        assertThat(actual, is(expected));
    }
}