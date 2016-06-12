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
import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class SecretKeySpecBase64KeysTest {

    private Base64Keys base64Keys;
    private Base64 base64;

    @Before
    public void setUp() {
        base64 = mock(Base64.class);
        base64Keys = new SecretKeySpecBase64Keys(base64);
    }

    @Test
    public void Can_encode_a_string() throws UnsupportedEncodingException {

        final SignatureAlgorithm algorithm = someEnum(SignatureAlgorithm.class);
        final byte[] secret = someString().getBytes();

        final String base64Secret = someString(1024);

        // Given
        given(base64.encode(secret)).willReturn(base64Secret);

        // When
        final Key actual = base64Keys.createKey(algorithm, secret);

        // Then
        assertThat(actual, instanceOf(SecretKeySpec.class));
        assertThat(actual.getAlgorithm(), equalTo(algorithm.getJcaName()));
        assertThat(actual.getEncoded(), equalTo(base64Secret.getBytes()));
    }
}