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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Karl Bennett
 */
public class CookieJwtLogoutHandler implements JwtLogoutHandler {

    private final String tokenName;
    private final Bakery<Cookie> bakery;

    public CookieJwtLogoutHandler(String tokenName, Bakery<Cookie> bakery) {
        this.tokenName = tokenName;
        this.bakery = bakery;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final Cookie cookie = bakery.bake(tokenName, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
