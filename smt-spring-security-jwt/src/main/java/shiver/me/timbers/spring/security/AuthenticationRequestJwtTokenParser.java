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
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Karl Bennett
 */
public class AuthenticationRequestJwtTokenParser implements JwtTokenParser<Authentication, HttpServletRequest> {

    private final String tokenName;
    private final String secret;
    private final JwtParser parser;

    public AuthenticationRequestJwtTokenParser(String tokenName, String secret, JwtParser parser) {
        this.tokenName = tokenName;
        this.secret = secret;
        this.parser = parser;
    }

    @Override
    public String create(Authentication authentication) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Authentication parse(HttpServletRequest request) throws JwtInvalidTokenException {
        try {
            return new JwtAuthentication(
                parser.setSigningKey(secret).parseClaimsJws(findToken(request)).getBody().get("principle").toString()
            );
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidTokenException("Could not find a JWT token in the request", e);
        } catch (JwtException e) {
            throw new JwtInvalidTokenException(e);
        }
    }

    private String findToken(HttpServletRequest request) {
        final String cookie = findCookieToken(request);
        if (cookie != null) {
            return cookie;
        }

        final String token = findHeaderToken(request);
        if (token != null) {
            return token;
        }

        return "";
    }

    private String findCookieToken(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String findHeaderToken(HttpServletRequest request) {
        return request.getHeader(tokenName);
    }
}
