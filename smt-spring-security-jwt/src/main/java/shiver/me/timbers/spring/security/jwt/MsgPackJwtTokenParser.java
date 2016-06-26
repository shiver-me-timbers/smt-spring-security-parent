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
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import org.msgpack.MessagePack;
import shiver.me.timbers.spring.security.Base64;
import shiver.me.timbers.spring.security.time.Clock;

import java.io.IOException;
import java.security.KeyPair;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
public class MsgPackJwtTokenParser<T> implements JwtTokenParser<T, String> {

    private static final String PRINCIPLE = "principle";

    private final Class<T> type;
    private final JwtBuilder builder;
    private final JwtParser parser;
    private final SignatureAlgorithm algorithm;
    private final KeyPair keyPair;
    private final int expiryDuration;
    private final TimeUnit expiryUnit;
    private final Clock clock;
    private final MessagePack messagePack;
    private final Base64 base64;

    public MsgPackJwtTokenParser(
        Class<T> type,
        JwtBuilder builder,
        JwtParser parser,
        SignatureAlgorithm algorithm,
        KeyPair keyPair,
        int expiryDuration,
        TimeUnit expiryUnit,
        Clock clock,
        MessagePack messagePack,
        Base64 base64
    ) {
        this.type = type;
        this.builder = builder;
        this.parser = parser;
        this.algorithm = algorithm;
        this.keyPair = keyPair;
        this.expiryDuration = expiryDuration;
        this.expiryUnit = expiryUnit;
        this.clock = clock;
        this.messagePack = messagePack;
        this.base64 = base64;
    }

    @Override
    public String create(T principle) throws JwtInvalidTokenException {
        try {
            final JwtBuilder signedBuilder = builder.claim(PRINCIPLE, base64.encode(messagePack.write(principle)))
                .signWith(algorithm, keyPair.getPrivate());
            if (expiryDuration >= 0) {
                return signedBuilder.setExpiration(clock.nowPlus(expiryDuration, expiryUnit)).compact();
            }
            return signedBuilder.compact();
        } catch (IOException e) {
            throw new JwtInvalidTokenException("Could not pack the JWT token principle into a " + type.getName(), e);
        }
    }

    @Override
    public T parse(String token) throws JwtInvalidTokenException {
        try {
            return messagePack.read(
                base64.decode(
                    parser.setSigningKey(keyPair.getPublic()).parseClaimsJws(token).getBody().get(PRINCIPLE).toString()
                ),
                type
            );
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidTokenException("Could not find a JWT token in the request", e);
        } catch (IOException e) {
            throw new JwtInvalidTokenException("Could not unpack the JWT token principle into a " + type.getName(), e);
        } catch (JwtException e) {
            throw new JwtInvalidTokenException(e);
        }
    }
}
