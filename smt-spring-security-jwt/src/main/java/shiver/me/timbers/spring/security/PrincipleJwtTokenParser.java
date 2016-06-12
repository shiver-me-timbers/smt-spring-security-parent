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

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;

/**
 * @author Karl Bennett
 */
public class PrincipleJwtTokenParser implements JwtTokenParser<String, String> {

    private final String secret;
    private final JwtParser parser;

    public PrincipleJwtTokenParser(String secret, JwtParser parser) {
        this.secret = secret;
        this.parser = parser;
    }

    @Override
    public String create(String principle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String parse(String token) throws JwtInvalidTokenException {
        try {
            return parser.setSigningKey(secret).parseClaimsJws(token).getBody().get("principle").toString();
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidTokenException("Could not find a JWT token in the request", e);
        } catch (JwtException e) {
            throw new JwtInvalidTokenException(e);
        }
    }
}
