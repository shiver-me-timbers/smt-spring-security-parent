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
import shiver.me.timbers.spring.security.Base64;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Karl Bennett
 */
public class SecretBase64KeyPairs implements Base64KeyPairs {

    private final Base64 base64;
    private final SignatureAlgorithm algorithm;

    public SecretBase64KeyPairs(Base64 base64, SignatureAlgorithm algorithm) {
        this.base64 = base64;
        this.algorithm = algorithm;
    }

    @Override
    public KeyPair createPair(String secret) throws IOException {
        final Base64Key key = new Base64Key(secret);
        return new KeyPair(key, key);
    }

    private class Base64Key implements PrivateKey, PublicKey, SecretKey {

        private final String secret;

        private Base64Key(String secret) {
            this.secret = secret;
        }

        @Override
        public String getAlgorithm() {
            return algorithm.getJcaName();
        }

        @Override
        public String getFormat() {
            return "RAW";
        }

        @Override
        public byte[] getEncoded() {
            return base64.encode(secret.getBytes()).getBytes();
        }
    }
}
