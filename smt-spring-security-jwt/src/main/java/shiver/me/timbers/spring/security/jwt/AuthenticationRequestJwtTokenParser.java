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

import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Karl Bennett
 */
public class AuthenticationRequestJwtTokenParser<T> implements JwtTokenParser<Authentication, HttpServletRequest> {

    private final String tokenName;
    private final AuthenticationConverter<T> authenticationConverter;
    private final JwtTokenParser<T, String> tokenParser;

    public AuthenticationRequestJwtTokenParser(
        String tokenName,
        AuthenticationConverter<T> authenticationConverter,
        JwtTokenParser<T, String> tokenParser
    ) {
        this.tokenName = tokenName;
        this.authenticationConverter = authenticationConverter;
        this.tokenParser = tokenParser;
    }

    @Override
    public String create(Authentication authentication) throws JwtInvalidTokenException {
        return tokenParser.create(authenticationConverter.convert(authentication));
    }

    @Override
    public Authentication parse(HttpServletRequest request) throws JwtInvalidTokenException {
        return authenticationConverter.convert(tokenParser.parse(findToken(request)));
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
