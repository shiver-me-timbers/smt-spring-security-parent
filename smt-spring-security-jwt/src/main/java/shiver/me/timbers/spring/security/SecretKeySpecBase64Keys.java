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

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * @author Karl Bennett
 */
public class SecretKeySpecBase64Keys implements Base64Keys {

    private final Base64 base64;

    public SecretKeySpecBase64Keys(Base64 base64) {
        this.base64 = base64;
    }

    @Override
    public Key createKey(SignatureAlgorithm algorithm, byte[] bytes) {
        return new SecretKeySpec(base64.encode(bytes).getBytes(), algorithm.getJcaName());
    }
}
