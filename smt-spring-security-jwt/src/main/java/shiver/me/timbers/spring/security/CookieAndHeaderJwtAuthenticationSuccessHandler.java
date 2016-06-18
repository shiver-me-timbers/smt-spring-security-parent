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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import shiver.me.timbers.spring.security.cookies.Bakery;
import shiver.me.timbers.spring.security.jwt.JwtTokenParser;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Karl Bennett
 */
public class CookieAndHeaderJwtAuthenticationSuccessHandler implements JwtAuthenticationSuccessHandler {

    private final String tokenName;
    private final JwtTokenParser<Authentication, ?> tokenParser;
    private final Bakery<Cookie> bakery;
    private AuthenticationSuccessHandler delegate;

    public CookieAndHeaderJwtAuthenticationSuccessHandler(
        String tokenName,
        JwtTokenParser<Authentication, ?> tokenParser,
        Bakery<Cookie> bakery
    ) {
        this(tokenName, tokenParser, bakery, null);
    }

    public CookieAndHeaderJwtAuthenticationSuccessHandler(
        String tokenName,
        JwtTokenParser tokenParser,
        Bakery<Cookie> bakery,
        AuthenticationSuccessHandler delegate
    ) {
        this.tokenName = tokenName;
        this.tokenParser = tokenParser;
        this.bakery = bakery;
        this.delegate = delegate;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        final String token = tokenParser.create(authentication);
        response.addHeader(tokenName, token);
        response.addCookie(bakery.bake(tokenName, token));
        if (delegate != null) {
            delegate.onAuthenticationSuccess(request, response, authentication);
        }
    }

    @Override
    public JwtAuthenticationSuccessHandler withDelegate(AuthenticationSuccessHandler delegate) {
        this.delegate = delegate;
        return this;
    }
}
