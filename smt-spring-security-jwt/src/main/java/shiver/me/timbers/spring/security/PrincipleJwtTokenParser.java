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

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
public class PrincipleJwtTokenParser implements JwtTokenParser<String, String> {

    private static final String PRINCIPLE = "principle";

    private final String secret;
    private final JwtBuilder builder;
    private final JwtParser parser;
    private final SignatureAlgorithm tokenHashing;
    private final KeySelector<SignatureAlgorithm> keySelector;
    private final int expiryDuration;
    private final TimeUnit expiryUnit;
    private final Clock clock;

    public PrincipleJwtTokenParser(
        String secret,
        JwtBuilder builder,
        JwtParser parser,
        SignatureAlgorithm tokenHashing,
        KeySelector<SignatureAlgorithm> keySelector,
        int expiryDuration,
        TimeUnit expiryUnit,
        Clock clock) {
        this.secret = secret;
        this.builder = builder;
        this.parser = parser;
        this.tokenHashing = tokenHashing;
        this.keySelector = keySelector;
        this.expiryDuration = expiryDuration;
        this.expiryUnit = expiryUnit;
        this.clock = clock;
    }

    @Override
    public String create(String principle) {
        final JwtBuilder signedBuilder = builder.claim(PRINCIPLE, principle)
            .signWith(tokenHashing, keySelector.select(tokenHashing, secret));
        if (expiryDuration >= 0) {
            return signedBuilder.setExpiration(clock.nowPlus(expiryDuration, expiryUnit)).compact();
        }
        return signedBuilder.compact();
    }

    @Override
    public String parse(String token) throws JwtInvalidTokenException {
        try {
            return parser.setSigningKey(keySelector.select(tokenHashing, secret)).parseClaimsJws(token).getBody()
                .get(PRINCIPLE).toString();
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidTokenException("Could not find a JWT token in the request", e);
        } catch (JwtException e) {
            throw new JwtInvalidTokenException(e);
        }
    }
}
