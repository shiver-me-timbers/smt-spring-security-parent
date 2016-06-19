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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import shiver.me.timbers.spring.security.modification.ChainModifier;
import shiver.me.timbers.spring.security.modification.LogoutHandlerAdder;
import shiver.me.timbers.spring.security.modification.SuccessHandlerWrapper;
import shiver.me.timbers.spring.security.weaving.ChainWeaver;
import shiver.me.timbers.spring.security.weaving.FilterChainProxyWeaver;
import shiver.me.timbers.spring.security.weaving.SecurityFilterChainWeaver;
import shiver.me.timbers.spring.security.weaving.Weaver;

import javax.servlet.Filter;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(JwtWeavingConfiguration.class)
public class JwtWeavingConfiguration {

    @Bean
    @ConditionalOnMissingBean(Weaver.class)
    @Autowired
    public Weaver weaver(FilterChainProxy filterChainProxy, ChainWeaver<SecurityFilterChain> chainWeaver) {
        return new FilterChainProxyWeaver(filterChainProxy, chainWeaver);
    }

    @Bean
    @ConditionalOnMissingBean(ChainWeaver.class)
    @Autowired
    public ChainWeaver<SecurityFilterChain> securityFilterChainWeaver(
        LogoutHandlerAdder logoutHandlerAdder,
        SuccessHandlerWrapper successHandlerWrapper,
        ChainModifier<SecurityFilterChain, Filter> modifier,
        JwtAuthenticationFilter authenticationFilter
    ) {
        return new SecurityFilterChainWeaver(logoutHandlerAdder, successHandlerWrapper, modifier, authenticationFilter);
    }
}