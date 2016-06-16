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

import java.io.IOException;
import java.security.KeyPair;

/**
 * @author Karl Bennett
 */
public class SignatureAlgorithmKeySelector implements KeySelector {

    private final SignatureAlgorithm algorithm;
    private final Base64KeyPairs base64KeyPairs;
    private final PemKeyPairs pemKeyPairs;

    public SignatureAlgorithmKeySelector(
        SignatureAlgorithm algorithm,
        Base64KeyPairs base64KeyPairs,
        PemKeyPairs pemKeyPairs
    ) {
        this.algorithm = algorithm;
        this.base64KeyPairs = base64KeyPairs;
        this.pemKeyPairs = pemKeyPairs;
    }

    @Override
    public KeyPair select(String secret) throws IOException {
        if (algorithm.isRsa() || algorithm.isEllipticCurve()) {
            return pemKeyPairs.createPair(secret);
        }
        return base64KeyPairs.createPair(secret);
    }
}
