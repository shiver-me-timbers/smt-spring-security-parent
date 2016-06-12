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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.Cookie;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(JwtConfiguration.class)
public class JwtConfiguration {

    @Value("${smt.spring.security.jwt.token.name:X-AUTH-TOKEN}")
    private String tokenName;

    @Bean
    @ConditionalOnMissingBean(JwtLogoutHandler.class)
    public JwtLogoutHandler jwtLogoutHandler() {
        return new CookieAndHeaderJwtLogoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationFilter.class)
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new CookieAndHeaderJwtAuthenticationFilter();
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationSuccessHandler.class)
    @Autowired
    public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler(
        JwtTokenParser tokenParser,
        Bakery<Cookie> bakery
    ) {
        return new CookieAndHeaderJwtAuthenticationSuccessHandler(tokenName, tokenParser, bakery);
    }

    @Bean
    @ConditionalOnMissingBean(JwtTokenParser.class)
    public JwtTokenParser jwtTokenParser() {
        return new JwtTokenParser();
    }

    @Bean
    @ConditionalOnMissingBean(Bakery.class)
    public Bakery<Cookie> bakery() {
        return new CookieBakery();
    }
}