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

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import shiver.me.timbers.spring.security.JwtAuthenticationFilter;
import shiver.me.timbers.spring.security.modification.ChainModifier;
import shiver.me.timbers.spring.security.modification.LogoutHandlerAdder;
import shiver.me.timbers.spring.security.modification.SuccessHandlerWrapper;

import javax.servlet.Filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SecurityFilterChainWeaverTest {

    private LogoutHandlerAdder logoutHandlerAdder;
    private SuccessHandlerWrapper successHandlerWrapper;
    private ChainModifier<SecurityFilterChain, Filter> modifier;
    private JwtAuthenticationFilter authenticationFilter;
    private ChainWeaver<SecurityFilterChain> chainWeaver;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        logoutHandlerAdder = mock(LogoutHandlerAdder.class);
        successHandlerWrapper = mock(SuccessHandlerWrapper.class);
        modifier = mock(ChainModifier.class);
        authenticationFilter = mock(JwtAuthenticationFilter.class);
        chainWeaver = new SecurityFilterChainWeaver(
            logoutHandlerAdder,
            successHandlerWrapper,
            modifier,
            authenticationFilter
        );
    }

    @Test
    public void Can_weave_a_security_filter_chain() {

        // Given
        final SecurityFilterChain filterChain = mock(SecurityFilterChain.class);

        // When
        chainWeaver.weave(filterChain);

        // Then
        verify(modifier).modifyLink(filterChain, LogoutFilter.class, logoutHandlerAdder);
        verify(modifier).addBefore(filterChain, UsernamePasswordAuthenticationFilter.class, authenticationFilter);
        verify(modifier).modifyLink(filterChain, UsernamePasswordAuthenticationFilter.class, successHandlerWrapper);
    }
}