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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.security.Security;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(SmtSpringSecurityJwtConfiguration.class)
@Import(JwtConfiguration.class)
public class SmtSpringSecurityJwtConfiguration {

    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private LogoutHandlerAdder logoutHandlerAdder;

    @Autowired
    private SuccessHandlerWrapper successHandlerWrapper;

    @Autowired
    private ChainModifier<Filter> modifier;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @PostConstruct
    public void configure() {
        Security.addProvider(new BouncyCastleProvider()); // Enable support for all the hashing algorithms.
        for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
            modifier.modifyLink(filterChain, LogoutFilter.class, logoutHandlerAdder);
            modifier.addBefore(filterChain, authenticationFilter, UsernamePasswordAuthenticationFilter.class);
            modifier.modifyLink(filterChain, UsernamePasswordAuthenticationFilter.class, successHandlerWrapper);
        }
    }
}