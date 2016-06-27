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
import io.jsonwebtoken.JwtException;

import java.security.KeyPair;
import java.util.Map;

import static shiver.me.timbers.spring.security.jwt.JwtEncryptor.PRINCIPAL;

/**
 * @author Karl Bennett
 */
public class JJwtDecryptor implements JwtDecryptor {

    private final JwtParserFactory parserFactory;
    private final KeyPair keyPair;
    private final ObjectMapper objectMapper;

    public JJwtDecryptor(JwtParserFactory parserFactory, KeyPair keyPair, ObjectMapper objectMapper) {
        this.parserFactory = parserFactory;
        this.keyPair = keyPair;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T decrypt(String token, Class<T> type) {
        try {
            return objectMapper.convertValue(
                parserFactory.create().setSigningKey(keyPair.getPublic()).parseClaimsJws(token)
                    .getBody().get(PRINCIPAL, Map.class),
                type
            );
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidTokenException("Could not find a JWT token in the request", e);
        } catch (JwtException e) {
            throw new JwtInvalidTokenException(e);
        }
    }
}
