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

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(JwtConfiguration.class)
public class JwtConfiguration {

    @Value("${smt.spring.security.jwt.tokenName:X-AUTH-TOKEN}")
    private String tokenName;

    @Value("${smt.spring.security.jwt.cookie.maxAgeDuration:-1}")
    private int maxAgeDuration;

    @Value("${smt.spring.security.jwt.cookie.maxAgeUnit:SECONDS}")
    private TimeUnit maxAgeUnit;

    @Value("${smt.spring.security.jwt.cookie.secure:false}")
    private boolean secure;

    @Value("${smt.spring.security.jwt.cookie.httpOnly:false}")
    private boolean httpOnly;

    @Value("${smt.spring.security.jwt.secret}")
    private String secret;

    @Bean
    @ConditionalOnMissingBean(JwtLogoutHandler.class)
    public JwtLogoutHandler jwtLogoutHandler() {
        return new CookieAndHeaderJwtLogoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationFilter.class)
    @Autowired
    public JwtAuthenticationFilter jwtAuthenticationFilter(
        JwtTokenParser<Authentication, HttpServletRequest> tokenParser,
        SecurityContextHolder securityContextHolder
    ) {
        return new CookieAndHeaderJwtAuthenticationFilter(tokenParser, securityContextHolder);
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationSuccessHandler.class)
    @Autowired
    public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler(
        JwtTokenParser<Authentication, HttpServletRequest> authenticationRequestJwtTokenParser,
        Bakery<Cookie> bakery
    ) {
        return new CookieAndHeaderJwtAuthenticationSuccessHandler(
            tokenName,
            authenticationRequestJwtTokenParser,
            bakery
        );
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationRequestJwtTokenParser.class)
    @Autowired
    public JwtTokenParser<Authentication, HttpServletRequest> authenticationRequestJwtTokenParser(
        JwtTokenParser<String, String> principleJwtTokenParser
    ) {
        return new AuthenticationRequestJwtTokenParser(tokenName, principleJwtTokenParser);
    }

    @Bean
    @ConditionalOnMissingBean(Bakery.class)
    public Bakery<Cookie> bakery() {
        return new CookieBakery(maxAgeDuration, maxAgeUnit, secure, httpOnly);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityContextHolder.class)
    public SecurityContextHolder securityContextHolder() {
        return new StaticSecurityContextHolder();
    }

    @Bean
    @ConditionalOnMissingBean(PrincipleJwtTokenParser.class)
    @Autowired
    public JwtTokenParser<String, String> principleJwtTokenParser(JwtBuilder builder, JwtParser parser) {
        return new PrincipleJwtTokenParser(secret, builder, parser);
    }

    @Bean
    @ConditionalOnMissingBean(JwtParser.class)
    public JwtParser jwtParser() {
        return Jwts.parser();
    }

    @Bean
    @ConditionalOnMissingBean(JwtBuilder.class)
    public JwtBuilder jwtBuilder() {
        return Jwts.builder();
    }
}