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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import shiver.me.timbers.spring.security.cookies.Bakery;
import shiver.me.timbers.spring.security.jwt.AuthenticationRequestJwtTokenParser;
import shiver.me.timbers.spring.security.jwt.JwtTokenParser;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CookieAndHeaderJwtAuthenticationSuccessHandlerTest {


    private String tokenName;
    private JwtTokenParser<Authentication, ?> tokenParser;
    private AuthenticationSuccessHandler delegate;
    private Bakery<Cookie> bakery;
    private JwtAuthenticationSuccessHandler successHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        tokenName = someString();
        tokenParser = mock(AuthenticationRequestJwtTokenParser.class);
        bakery = mock(Bakery.class);
        delegate = mock(AuthenticationSuccessHandler.class);
        successHandler = new CookieAndHeaderJwtAuthenticationSuccessHandler(tokenName, tokenParser, bakery, delegate);
    }

    @Test
    public void Can_handle_a_successful_authentication() throws IOException, ServletException {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        final String token = someString();
        final Cookie cookie = mock(Cookie.class);

        // Given
        given(tokenParser.create(authentication)).willReturn(token);
        given(bakery.bake(tokenName, token)).willReturn(cookie);

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        final InOrder order = inOrder(response, delegate);
        order.verify(response).addHeader(tokenName, token);
        order.verify(response).addCookie(cookie);
        order.verify(delegate).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    public void Can_handle_a_successful_authentication_with_no_delegate() throws IOException, ServletException {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        final String token = someString();
        final Cookie cookie = mock(Cookie.class);

        // Given
        given(tokenParser.create(authentication)).willReturn(token);
        given(bakery.bake(tokenName, token)).willReturn(cookie);

        // When
        new CookieAndHeaderJwtAuthenticationSuccessHandler(tokenName, tokenParser, bakery)
            .onAuthenticationSuccess(request, response, authentication);

        // Then
        final InOrder order = inOrder(response, delegate);
        order.verify(response).addHeader(tokenName, token);
        order.verify(response).addCookie(cookie);
    }

    @Test
    public void Can_update_the_delegate() throws IOException, ServletException {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        final String token = someString();
        final Cookie cookie = mock(Cookie.class);
        final AuthenticationSuccessHandler delegate = mock(AuthenticationSuccessHandler.class);

        // Given
        given(tokenParser.create(authentication)).willReturn(token);
        given(bakery.bake(tokenName, token)).willReturn(cookie);
        successHandler.withDelegate(delegate);

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        final InOrder order = inOrder(response, delegate);
        order.verify(response).addHeader(tokenName, token);
        order.verify(response).addCookie(cookie);
        order.verify(delegate).onAuthenticationSuccess(request, response, authentication);
    }
}