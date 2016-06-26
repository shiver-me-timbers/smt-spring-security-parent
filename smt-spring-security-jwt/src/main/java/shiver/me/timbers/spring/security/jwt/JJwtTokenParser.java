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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import shiver.me.timbers.spring.security.time.Clock;

import java.security.KeyPair;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
public class JJwtTokenParser<T> implements JwtTokenParser<T, String> {

    private static final String PRINCIPAL = "principal";

    private final Class<T> type;
    private final JwtBuilder builder;
    private final JwtParser parser;
    private final SignatureAlgorithm algorithm;
    private final KeyPair keyPair;
    private final int expiryDuration;
    private final TimeUnit expiryUnit;
    private final Clock clock;
    private final ObjectMapper objectMapper;

    public JJwtTokenParser(
        Class<T> type,
        JwtBuilder builder,
        JwtParser parser,
        SignatureAlgorithm algorithm,
        KeyPair keyPair,
        int expiryDuration,
        TimeUnit expiryUnit,
        Clock clock,
        ObjectMapper objectMapper
    ) {
        this.type = type;
        this.builder = builder;
        this.parser = parser;
        this.algorithm = algorithm;
        this.keyPair = keyPair;
        this.expiryDuration = expiryDuration;
        this.expiryUnit = expiryUnit;
        this.clock = clock;
        this.objectMapper = objectMapper;
    }

    @Override
    public String create(T principal) {
        final JwtBuilder signedBuilder = builder.claim(PRINCIPAL, principal)
            .signWith(algorithm, keyPair.getPrivate());
        if (expiryDuration >= 0) {
            return signedBuilder.setExpiration(clock.nowPlus(expiryDuration, expiryUnit)).compact();
        }
        return signedBuilder.compact();
    }

    @Override
    public T parse(String token) throws JwtInvalidTokenException {
        try {
            return objectMapper.convertValue(
                parser.setSigningKey(keyPair.getPublic()).parseClaimsJws(token).getBody().get(PRINCIPAL, Map.class),
                type
            );
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidTokenException("Could not find a JWT token in the request", e);
        } catch (JwtException e) {
            throw new JwtInvalidTokenException(e);
        }
    }
}
