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

import java.security.InvalidKeyException;
import java.security.Key;

/**
 * @author Karl Bennett
 */
public class SignatureAlgorithmKeySelector implements KeySelector<SignatureAlgorithm> {

    private final Base64Keys base64Keys;
    private final RsaKeys rsaKeys;

    public SignatureAlgorithmKeySelector(Base64Keys base64Keys, RsaKeys rsaKeys) {
        this.base64Keys = base64Keys;
        this.rsaKeys = rsaKeys;
    }

    @Override
    public Key select(SignatureAlgorithm algorithm, String secret) {
        if (algorithm.isRsa()) {
            try {
                return rsaKeys.createKey(secret);
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return base64Keys.createKey(algorithm, secret.getBytes());
    }
}
