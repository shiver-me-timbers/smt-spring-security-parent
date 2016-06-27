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
import shiver.me.timbers.spring.security.time.Clock;

import java.security.KeyPair;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
public class JJwtEncryptor implements JwtEncryptor {

    private final JwtBuilder builder;
    private final SignatureAlgorithm algorithm;
    private final KeyPair keyPair;
    private final int expiryDuration;
    private final TimeUnit expiryUnit;
    private final Clock clock;

    public JJwtEncryptor(
        JwtBuilder builder,
        SignatureAlgorithm algorithm,
        KeyPair keyPair,
        int expiryDuration,
        TimeUnit expiryUnit,
        Clock clock
    ) {
        this.builder = builder;
        this.algorithm = algorithm;
        this.keyPair = keyPair;
        this.expiryDuration = expiryDuration;
        this.expiryUnit = expiryUnit;
        this.clock = clock;
    }

    @Override
    public String encrypt(Object principal) {
        final JwtBuilder signedBuilder = builder.claim(PRINCIPAL, principal)
            .signWith(algorithm, keyPair.getPrivate());
        if (expiryDuration >= 0) {
            return signedBuilder.setExpiration(clock.nowPlus(expiryDuration, expiryUnit)).compact();
        }
        return signedBuilder.compact();
    }
}
