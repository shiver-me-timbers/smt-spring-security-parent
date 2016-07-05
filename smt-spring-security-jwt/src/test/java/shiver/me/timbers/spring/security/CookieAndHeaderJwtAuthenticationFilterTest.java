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
import org.springframework.security.core.context.SecurityContext;
import shiver.me.timbers.spring.security.context.SecurityContextHolder;
import shiver.me.timbers.spring.security.jwt.JwtInvalidTokenException;
import shiver.me.timbers.spring.security.jwt.JwtTokenParser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.Boolean.TRUE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static shiver.me.timbers.spring.security.CookieAndHeaderJwtAuthenticationFilter.FILTER_APPLIED;

public class CookieAndHeaderJwtAuthenticationFilterTest {

    private JwtTokenParser<Authentication, HttpServletRequest> tokenParser;
    private SecurityContextHolder securityContextHolder;
    private JwtAuthenticationApplier authenticationApplier;
    private CookieAndHeaderJwtAuthenticationFilter filter;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        tokenParser = mock(JwtTokenParser.class);
        securityContextHolder = mock(SecurityContextHolder.class);
        authenticationApplier = mock(JwtAuthenticationApplier.class);
        filter = new CookieAndHeaderJwtAuthenticationFilter(tokenParser, securityContextHolder, authenticationApplier);
    }

    @Test
    public void Can_authenticate_a_request() throws IOException, ServletException, JwtInvalidTokenException {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);

        final Authentication authentication = mock(Authentication.class);
        final SecurityContext securityContext = mock(SecurityContext.class);

        // Given
        given(tokenParser.parse(request)).willReturn(authentication);
        given(securityContextHolder.getContext()).willReturn(securityContext);

        // When
        filter.doFilter(request, response, chain);

        // Then
        final InOrder order = inOrder(securityContext, authenticationApplier, request, chain);
        order.verify(securityContext).setAuthentication(authentication);
        order.verify(authenticationApplier).apply(authentication, response);
        order.verify(request).setAttribute(FILTER_APPLIED, TRUE);
        order.verify(chain).doFilter(request, response);
    }

    @Test
    public void Can_only_authenticate_a_request_once() throws IOException, ServletException, JwtInvalidTokenException {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);

        // Given
        given(request.getAttribute(FILTER_APPLIED)).willReturn(TRUE);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(chain).doFilter(request, response);
        verifyZeroInteractions(tokenParser, securityContextHolder, authenticationApplier);
    }

    @Test
    public void Can_fail_authenticate_a_request() throws IOException, ServletException, JwtInvalidTokenException {

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);

        final SecurityContext securityContext = mock(SecurityContext.class);

        // Given
        given(tokenParser.parse(request)).willThrow(new JwtInvalidTokenException(new Exception()));
        given(securityContextHolder.getContext()).willReturn(securityContext);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verifyZeroInteractions(securityContext, authenticationApplier);
        verify(chain).doFilter(request, response);
    }
}