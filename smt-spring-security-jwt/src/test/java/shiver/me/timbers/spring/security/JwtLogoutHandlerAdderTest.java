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

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import shiver.me.timbers.spring.security.fields.FieldMutator;
import shiver.me.timbers.spring.security.fields.Updater;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

public class JwtLogoutHandlerAdderTest {

    private FieldMutator mutator;
    private JwtLogoutHandler logoutHandler;
    private LogoutHandlerAdder logoutHandlerAdder;

    @Before
    public void setUp() {
        mutator = mock(FieldMutator.class);
        logoutHandler = mock(JwtLogoutHandler.class);
        logoutHandlerAdder = new JwtLogoutHandlerAdder(mutator, logoutHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_add_a_logout_handler() {

        final LogoutFilter filter = mock(LogoutFilter.class);

        final CaptureHandlers handlers = new CaptureHandlers();

        // Given
        willAnswer(handlers).given(mutator).update(eq(filter), eq("handlers"), eq(List.class), any(Updater.class));

        // When
        logoutHandlerAdder.modify(filter);

        // Then
        assertThat(handlers, contains((LogoutHandler) logoutHandler));
    }

    private static class CaptureHandlers extends ArrayList<LogoutHandler> implements Answer {

        @SuppressWarnings("unchecked")
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            addAll(((Updater<List>) invocation.getArguments()[3]).update(emptyList()));
            return null;
        }
    }
}