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

package shiver.me.timbers.spring.security.weaving;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import shiver.me.timbers.spring.security.JwtAuthenticationFilter;
import shiver.me.timbers.spring.security.modification.ChainModifier;
import shiver.me.timbers.spring.security.modification.LogoutHandlerAdder;
import shiver.me.timbers.spring.security.modification.SuccessHandlerWrapper;

import javax.servlet.Filter;

/**
 * @author Karl Bennett
 */
public class SecurityFilterChainWeaver implements ChainWeaver<SecurityFilterChain> {

    private final LogoutHandlerAdder logoutHandlerAdder;
    private final SuccessHandlerWrapper successHandlerWrapper;
    private final ChainModifier<SecurityFilterChain, Filter> modifier;
    private final JwtAuthenticationFilter authenticationFilter;

    public SecurityFilterChainWeaver(
        LogoutHandlerAdder logoutHandlerAdder,
        SuccessHandlerWrapper successHandlerWrapper,
        ChainModifier<SecurityFilterChain, Filter> modifier,
        JwtAuthenticationFilter authenticationFilter
    ) {
        this.logoutHandlerAdder = logoutHandlerAdder;
        this.successHandlerWrapper = successHandlerWrapper;
        this.modifier = modifier;
        this.authenticationFilter = authenticationFilter;
    }

    @Override
    public void weave(SecurityFilterChain filterChain) {
        modifier.modifyLink(filterChain, LogoutFilter.class, logoutHandlerAdder);
        modifier.addBefore(filterChain, UsernamePasswordAuthenticationFilter.class, authenticationFilter);
        modifier.modifyLink(filterChain, UsernamePasswordAuthenticationFilter.class, successHandlerWrapper);
    }
}