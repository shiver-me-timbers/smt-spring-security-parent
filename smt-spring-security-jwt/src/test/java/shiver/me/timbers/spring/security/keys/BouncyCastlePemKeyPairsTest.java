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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.spring.security.TestFiles.readFile;

public class BouncyCastlePemKeyPairsTest {

    private BouncyCastlePemKeyPairs keyPairs;

    @BeforeClass
    public static void classSetUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Before
    public void setUp() {
        keyPairs = new BouncyCastlePemKeyPairs();
    }

    @Test
    public void Can_create_an_rsa_key_pair() throws IOException {

        // When
        final KeyPair actual = keyPairs.createPair(privateKey("rsa"));

        // Then
        assertThat(actual.getPrivate(), instanceOf(RSAPrivateKey.class));
        assertThat(actual.getPublic(), instanceOf(RSAPublicKey.class));
    }

    @Test
    public void Can_create_an_ecdsa_key_pair() throws IOException {

        // When
        final KeyPair actual = keyPairs.createPair(privateKey("ecdsa"));

        // Then
        assertThat(actual.getPrivate(), instanceOf(ECPrivateKey.class));
        assertThat(actual.getPublic(), instanceOf(ECPublicKey.class));
    }

    private static String privateKey(String name) {
        return readFile("id_" + name);
    }
}