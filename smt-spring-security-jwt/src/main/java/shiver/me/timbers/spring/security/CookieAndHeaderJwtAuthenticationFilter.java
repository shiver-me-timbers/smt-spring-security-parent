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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.GenericFilterBean;
import shiver.me.timbers.spring.security.jwt.JwtInvalidTokenException;
import shiver.me.timbers.spring.security.jwt.JwtTokenParser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Karl Bennett
 */
public class CookieAndHeaderJwtAuthenticationFilter extends GenericFilterBean implements JwtAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JwtTokenParser<Authentication, HttpServletRequest> tokenParser;
    private final SecurityContextHolder securityContextHolder;

    public CookieAndHeaderJwtAuthenticationFilter(
        JwtTokenParser<Authentication, HttpServletRequest> tokenParser,
        SecurityContextHolder securityContextHolder
    ) {
        this.tokenParser = tokenParser;
        this.securityContextHolder = securityContextHolder;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            securityContextHolder.getContext().setAuthentication(tokenParser.parse((HttpServletRequest) request));
        } catch (JwtInvalidTokenException e) {
            log.debug("Failed JWT authentication.", e);
        }
        chain.doFilter(request, response);
    }
}
