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

import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.security.core.Authentication;
import shiver.me.timbers.spring.security.cookies.Bakery;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaNumericString;

public class CookieJwtLogoutHandlerTest {

    @Test
    public void Can_remove_the_jwt_cookie() {

        @SuppressWarnings("unchecked")
        final Bakery<Cookie> bakery = mock(Bakery.class);
        final String tokenName = someAlphaNumericString(8);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        final Cookie cookie = mock(Cookie.class);

        // Given
        given(bakery.bake(tokenName, "")).willReturn(cookie);

        // When
        new CookieJwtLogoutHandler(tokenName, bakery)
            .logout(mock(HttpServletRequest.class), response, mock(Authentication.class));

        // Then
        final InOrder order = inOrder(cookie, response);
        order.verify(cookie).setMaxAge(0);
        order.verify(response).addCookie(cookie);
    }
}