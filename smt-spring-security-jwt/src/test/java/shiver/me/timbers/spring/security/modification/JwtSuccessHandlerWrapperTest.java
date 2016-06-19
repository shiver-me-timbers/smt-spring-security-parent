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

package shiver.me.timbers.spring.security.modification;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shiver.me.timbers.spring.security.JwtAuthenticationSuccessHandler;
import shiver.me.timbers.spring.security.fields.FieldMutator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JwtSuccessHandlerWrapperTest {

    private FieldMutator mutator;
    private JwtAuthenticationSuccessHandler successHandler;
    private SuccessHandlerWrapper successHandlerWrapper;

    @Before
    public void setUp() {
        mutator = mock(FieldMutator.class);
        successHandler = mock(JwtAuthenticationSuccessHandler.class);
        successHandlerWrapper = new JwtSuccessHandlerWrapper(mutator, successHandler);
    }

    @Test
    public void Can_wrap_a_success_handler() {

        final UsernamePasswordAuthenticationFilter filter = mock(UsernamePasswordAuthenticationFilter.class);

        final AuthenticationSuccessHandler oldSuccessHandler = mock(AuthenticationSuccessHandler.class);
        final JwtAuthenticationSuccessHandler newSuccessHandler = mock(JwtAuthenticationSuccessHandler.class);

        // Given
        given(mutator.retrieve(filter, "successHandler", AuthenticationSuccessHandler.class))
            .willReturn(oldSuccessHandler);
        given(successHandler.withDelegate(oldSuccessHandler)).willReturn(newSuccessHandler);

        // When
        successHandlerWrapper.modify(filter);

        // Then
        verify(filter).setAuthenticationSuccessHandler(newSuccessHandler);
    }
}