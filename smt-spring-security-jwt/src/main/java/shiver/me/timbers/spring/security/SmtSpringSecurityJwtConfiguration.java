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
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(SmtSpringSecurityJwtConfiguration.class)
public class SmtSpringSecurityJwtConfiguration {

    @Value("${smt.spring.security.jwt.token.name:X-AUTH-TOKEN}")
    private String tokenName;

    @Autowired
    private ChainConfigurer<Filter> configurer;

    @Autowired
    private JwtTokenParser tokenParser;

    @Autowired
    private Bakery<Cookie> bakery;

    @Autowired
    private FieldMutator extractor;

    @PostConstruct
    public void configure() {
        configurer.updateFilters(
            UsernamePasswordAuthenticationFilter.class,
            new Updater<UsernamePasswordAuthenticationFilter>() {
                @Override
                public void update(final UsernamePasswordAuthenticationFilter filter) {
                    filter.setAuthenticationSuccessHandler(
                        new CookieAndHeaderJwtAuthenticationSuccessHandler(
                            tokenName,
                            tokenParser,
                            bakery,
                            extractor.retrieve(filter, "successHandler", AuthenticationSuccessHandler.class)
                        )
                    );
                }
            }
        );
        configurer.addBefore(new CookieAndHeaderJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        configurer.updateFilters(
            LogoutFilter.class,
            new Updater<LogoutFilter>() {
                @SuppressWarnings("unchecked")
                @Override
                public void update(final LogoutFilter filter) {
                    final List<LogoutHandler> handlers = new ArrayList<>(
                        extractor.retrieve(filter, "handlers", List.class)
                    );
                    handlers.add(0, new CookieAndHeaderJwtLogoutHandler());
                    extractor.update(
                        filter, "handlers", List.class, asList(handlers.toArray(new LogoutHandler[handlers.size()]))
                    );
                }
            }
        );
    }

    @Bean
    @ConditionalOnMissingBean(ChainConfigurer.class)
    @Autowired
    public ChainConfigurer<Filter> securityFilterChainConfigurer(FilterChainProxy filterChainProxy) {
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
    @ConditionalOnMissingBean(FieldMutator.class)
    @Autowired
    public FieldMutator fieldExtractor(FieldFinder fieldFinder, FieldGetter fieldGetter, FieldSetter fieldSetter) {
        return new ReflectionFieldMutator(fieldFinder, fieldGetter, fieldSetter);
    }

    @Bean
    @ConditionalOnMissingBean(FieldFinder.class)
    public FieldFinder fieldFinder() {
        return new ReflectionFieldFinder();
    }

    @Bean
    @ConditionalOnMissingBean(FieldGetter.class)
    public FieldGetter fieldGetter() {
        return new ReflectionFieldGetter();
    }

    @Bean
    @ConditionalOnMissingBean(FieldSetter.class)
    public FieldSetter fieldSetter() {
        return new ReflectionFieldSetter();
    }
}