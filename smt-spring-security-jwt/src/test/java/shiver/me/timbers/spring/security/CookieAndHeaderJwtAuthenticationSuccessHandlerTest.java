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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CookieAndHeaderJwtAuthenticationSuccessHandlerTest {


    private JwtAuthenticationApplier authenticationApplier;
    private AuthenticationSuccessHandler delegate;
    private JwtAuthenticationSuccessHandler successHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        delegate = mock(AuthenticationSuccessHandler.class);
        authenticationApplier = mock(JwtAuthenticationApplier.class);
        successHandler = new CookieAndHeaderJwtAuthenticationSuccessHandler(authenticationApplier, delegate);
    }

    @Test
    public void Can_handle_a_successful_authentication() throws IOException, ServletException {

        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        final InOrder order = inOrder(authenticationApplier, delegate);
        order.verify(authenticationApplier).apply(authentication, response);
        order.verify(delegate).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    public void Can_handle_a_successful_authentication_with_no_delegate() throws IOException, ServletException {

        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        // When
        new CookieAndHeaderJwtAuthenticationSuccessHandler(authenticationApplier)
            .onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(authenticationApplier).apply(authentication, response);
    }

    @Test
    public void Can_update_the_delegate() throws IOException, ServletException {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final Authentication authentication = mock(Authentication.class);

        final AuthenticationSuccessHandler delegate = mock(AuthenticationSuccessHandler.class);

        // Given
        successHandler.withDelegate(delegate);

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        final InOrder order = inOrder(authenticationApplier, delegate);
        order.verify(authenticationApplier).apply(authentication, response);
        order.verify(delegate).onAuthenticationSuccess(request, response, authentication);
    }
}