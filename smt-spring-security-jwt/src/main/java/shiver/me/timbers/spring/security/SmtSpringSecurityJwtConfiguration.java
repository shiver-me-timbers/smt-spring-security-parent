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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(SmtSpringSecurityJwtConfiguration.class)
public class SmtSpringSecurityJwtConfiguration {

    @Value("${smt.spring.security.jwt.token.name:X-AUTH-TOKEN}")
    private String tokenName;

    @Autowired
    private SecurityFilterChainConfigurer configurer;

    @Autowired
    private JwtTokenParser tokenParser;

    @Autowired
    private Bakery<Cookie> bakery;

    @Autowired
    private FieldExtractor extractor;

    @PostConstruct
    public void configure() {
        configurer.updateFilters(
            UsernamePasswordAuthenticationFilter.class,
            new Updater<UsernamePasswordAuthenticationFilter>() {
                @Override
                public void update(final UsernamePasswordAuthenticationFilter filter) {
                    filter.setAuthenticationSuccessHandler(
                        new JwtAuthenticationSuccessHandler(
                            tokenName,
                            tokenParser,
                            bakery,
                            extractor.extract(AuthenticationSuccessHandler.class, "successHandler", filter)
                        )
                    );
                }
            }
        );
        configurer.addBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        configurer.addBefore(new JwtLogoutFilter(), LogoutFilter.class);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChainConfigurer.class)
    @Autowired
    public SecurityFilterChainConfigurer securityFilterChainConfigurer(FilterChainProxy filterChainProxy) {
        return new SecurityFilterChainConfigurer(filterChainProxy);
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

    @Bean
    @ConditionalOnMissingBean(FieldExtractor.class)
    @Autowired
    public FieldExtractor fieldExtractor(FieldGetter fieldGetter) {
        return new ReflectionFieldExtractor(fieldGetter);
    }

    @Bean
    @ConditionalOnMissingBean(FieldGetter.class)
    public FieldGetter fieldGetter() {
        return new FieldGetter();
    }
}