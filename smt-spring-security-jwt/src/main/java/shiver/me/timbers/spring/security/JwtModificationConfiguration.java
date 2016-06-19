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
import org.springframework.security.web.SecurityFilterChain;
import shiver.me.timbers.spring.security.fields.FieldFinder;
import shiver.me.timbers.spring.security.fields.FieldGetSetter;
import shiver.me.timbers.spring.security.fields.FieldMutator;
import shiver.me.timbers.spring.security.fields.ReflectionFieldFinder;
import shiver.me.timbers.spring.security.fields.ReflectionFieldGetSetter;
import shiver.me.timbers.spring.security.fields.ReflectionFieldMutator;
import shiver.me.timbers.spring.security.modification.ChainModifier;
import shiver.me.timbers.spring.security.modification.JwtLogoutHandlerAdder;
import shiver.me.timbers.spring.security.modification.JwtSuccessHandlerWrapper;
import shiver.me.timbers.spring.security.modification.LogoutHandlerAdder;
import shiver.me.timbers.spring.security.modification.SecurityFilterChainModifier;
import shiver.me.timbers.spring.security.modification.SuccessHandlerWrapper;

import javax.servlet.Filter;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(JwtModificationConfiguration.class)
public class JwtModificationConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogoutHandlerAdder.class)
    @Autowired
    public LogoutHandlerAdder logoutHandlerAdder(FieldMutator mutator, JwtLogoutHandler logoutHandler) {
        return new JwtLogoutHandlerAdder(mutator, logoutHandler);
    }

    @Bean
    @ConditionalOnMissingBean(SuccessHandlerWrapper.class)
    @Autowired
    public SuccessHandlerWrapper successHandlerWrapper(
        FieldMutator mutator,
        JwtAuthenticationSuccessHandler successHandler
    ) {
        return new JwtSuccessHandlerWrapper(mutator, successHandler);
    }

    @Bean
    @ConditionalOnMissingBean(ChainModifier.class)
    public ChainModifier<SecurityFilterChain, Filter> modifier() {
        return new SecurityFilterChainModifier();
    }

    @Bean
    @ConditionalOnMissingBean(FieldMutator.class)
    @Autowired
    public FieldMutator mutator(FieldFinder fieldFinder, FieldGetSetter fieldGetSetter) {
        return new ReflectionFieldMutator(fieldFinder, fieldGetSetter);
    }

    @Bean
    @ConditionalOnMissingBean(FieldFinder.class)
    public FieldFinder fieldFinder() {
        return new ReflectionFieldFinder();
    }

    @Bean
    @ConditionalOnMissingBean(FieldGetSetter.class)
    public FieldGetSetter fieldGetSetter() {
        return new ReflectionFieldGetSetter();
    }
}