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

import org.springframework.security.core.Authentication;
import shiver.me.timbers.spring.security.cookies.Bakery;
import shiver.me.timbers.spring.security.jwt.JwtTokenParser;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Karl Bennett
 */
public class CookieJwtAuthenticationApplier implements JwtAuthenticationApplier {
    private final String tokenName;
    private final JwtTokenParser<Authentication, ?> tokenParser;
    private final Bakery<Cookie> bakery;

    public CookieJwtAuthenticationApplier(
        String tokenName,
        JwtTokenParser<Authentication, ?> tokenParser,
        Bakery<Cookie> bakery
    ) {
        this.tokenName = tokenName;
        this.tokenParser = tokenParser;
        this.bakery = bakery;
    }

    @Override
    public void apply(Authentication authentication, HttpServletResponse response) {
        final String token = tokenParser.create(authentication);
        response.setHeader(tokenName, token);
        response.addCookie(bakery.bake(tokenName, token));
    }
}
