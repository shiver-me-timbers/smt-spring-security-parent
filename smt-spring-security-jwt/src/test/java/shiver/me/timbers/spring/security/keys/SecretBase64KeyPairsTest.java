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
import org.junit.Test;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class SecretBase64KeyPairsTest {

    @Test
    public void Can_create_a_base64_key_pair() throws IOException {

        final Base64 base64 = mock(Base64.class);
        final SignatureAlgorithm algorithm = someEnum(SignatureAlgorithm.class);
        final String secret = someString();

        final String base64Secret = someString();

        // Given
        given(base64.encode(eq(secret.getBytes()))).willReturn(base64Secret);

        // When
        final KeyPair actual = new SecretBase64KeyPairs(base64, algorithm).createPair(secret);

        // Then
        final PrivateKey privateKey = actual.getPrivate();
        final PublicKey publicKey = actual.getPublic();
        assertThat(privateKey.getEncoded(), equalTo(base64Secret.getBytes()));
        assertThat(privateKey.getAlgorithm(), equalTo(algorithm.getJcaName()));
        assertThat(privateKey.getFormat(), equalTo("RAW"));
        assertThat(publicKey.getEncoded(), equalTo(base64Secret.getBytes()));
        assertThat(publicKey.getAlgorithm(), equalTo(algorithm.getJcaName()));
        assertThat(publicKey.getFormat(), equalTo("RAW"));
    }
}