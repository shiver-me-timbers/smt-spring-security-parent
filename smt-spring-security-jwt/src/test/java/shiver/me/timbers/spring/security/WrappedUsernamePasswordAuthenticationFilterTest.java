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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shiver.me.timbers.spring.security.fields.ReflectionFieldFinder;
import shiver.me.timbers.spring.security.fields.ReflectionFieldGetSetter;
import shiver.me.timbers.spring.security.fields.ReflectionFieldMutator;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.matchers.Matchers.hasField;

public class WrappedUsernamePasswordAuthenticationFilterTest {

    private ReflectionFieldMutator reflectionFieldMutator;
    private UsernamePasswordAuthenticationFilter filter;
    private JwtAuthenticationSuccessHandler jwtSuccessHandler;

    @Before
    public void setUp() {
        reflectionFieldMutator = new ReflectionFieldMutator(new ReflectionFieldFinder(), new ReflectionFieldGetSetter());
        filter = new UsernamePasswordAuthenticationFilter();
        jwtSuccessHandler = mock(JwtAuthenticationSuccessHandler.class);
    }

    /**
     * This test isn't technically needed, but I really want to make sure everything is copied.
     */
    @Test
    public void Can_wrap_a_username_password_authentication_filter() throws IllegalAccessException {

        final JwtAuthenticationSuccessHandler withSuccessHandler = mock(JwtAuthenticationSuccessHandler.class);

        // Given
        final Object usernameParameter = extractFiledValue(filter, "usernameParameter");
        final Object passwordParameter = extractFiledValue(filter, "passwordParameter");
        final Object postOnly = extractFiledValue(filter, "postOnly");
        final Object eventPublisher = extractFiledValue(filter, "eventPublisher");
        final Object authenticationDetailsSource = extractFiledValue(filter, "authenticationDetailsSource");
        final Object authenticationManager = extractFiledValue(filter, "authenticationManager");
        final Object messages = extractFiledValue(filter, "messages");
        final Object rememberMeServices = extractFiledValue(filter, "rememberMeServices");
        final Object requiresAuthenticationRequestMatcher = extractFiledValue(filter, "requiresAuthenticationRequestMatcher");
        final Object continueChainBeforeSuccessfulAuthentication = extractFiledValue(filter, "continueChainBeforeSuccessfulAuthentication");
        final Object sessionStrategy = extractFiledValue(filter, "sessionStrategy");
        final Object allowSessionCreation = extractFiledValue(filter, "allowSessionCreation");
        final Object successHandler = extractFiledValue(filter, "successHandler");
        final Object failureHandler = extractFiledValue(filter, "failureHandler");
        final Object requiredProperties = extractFiledValue(filter, "requiredProperties");
        final Object filterConfig = extractFiledValue(filter, "filterConfig");
        final Object beanName = extractFiledValue(filter, "beanName");
        final Object environment = extractFiledValue(filter, "environment");
        final Object servletContext = extractFiledValue(filter, "servletContext");
        given(jwtSuccessHandler.withDelegate((AuthenticationSuccessHandler) successHandler)).willReturn(withSuccessHandler);

        // When
        final WrappedUsernamePasswordAuthenticationFilter wrappedFilter
            = new WrappedUsernamePasswordAuthenticationFilter(reflectionFieldMutator, filter, jwtSuccessHandler);

        // Then
        assertThat(wrappedFilter, hasField("usernameParameter", usernameParameter));
        assertThat(wrappedFilter, hasField("passwordParameter", passwordParameter));
        assertThat(wrappedFilter, hasField("postOnly", postOnly));
        assertThat(wrappedFilter, hasField("eventPublisher", eventPublisher));
        assertThat(wrappedFilter, hasField("authenticationDetailsSource", authenticationDetailsSource));
        assertThat(wrappedFilter, hasField("authenticationManager", authenticationManager));
        assertThat(wrappedFilter, hasField("messages", messages));
        assertThat(wrappedFilter, hasField("rememberMeServices", rememberMeServices));
        assertThat(wrappedFilter, hasField("requiresAuthenticationRequestMatcher", requiresAuthenticationRequestMatcher));
        assertThat(wrappedFilter, hasField("continueChainBeforeSuccessfulAuthentication", continueChainBeforeSuccessfulAuthentication));
        assertThat(wrappedFilter, hasField("sessionStrategy", sessionStrategy));
        assertThat(wrappedFilter, hasField("allowSessionCreation", allowSessionCreation));
        assertThat(wrappedFilter, hasField("successHandler", withSuccessHandler));
        assertThat(wrappedFilter, hasField("failureHandler", failureHandler));
        assertThat(wrappedFilter, hasField("requiredProperties", requiredProperties));
        assertThat(wrappedFilter, hasField("filterConfig", filterConfig));
        assertThat(wrappedFilter, hasField("beanName", beanName));
        assertThat(wrappedFilter, hasField("environment", environment));
        assertThat(wrappedFilter, hasField("servletContext", servletContext));
    }

    @Test
    public void Can_set_a_wrapped_authentication_success_handler() throws IllegalAccessException {

        final AuthenticationSuccessHandler newSuccessHandler = mock(AuthenticationSuccessHandler.class);

        final JwtAuthenticationSuccessHandler withSuccessHandler = mock(JwtAuthenticationSuccessHandler.class);

        // Given
        final Object successHandler = extractFiledValue(filter, "successHandler");
        given(jwtSuccessHandler.withDelegate((AuthenticationSuccessHandler) successHandler))
            .willReturn(withSuccessHandler);

        // When
        final WrappedUsernamePasswordAuthenticationFilter wrappedFilter
            = new WrappedUsernamePasswordAuthenticationFilter(reflectionFieldMutator, filter, jwtSuccessHandler);
        wrappedFilter.setAuthenticationSuccessHandler(newSuccessHandler);

        // Then
        verify(jwtSuccessHandler).withDelegate(newSuccessHandler);
        assertThat(wrappedFilter.getSuccessHandler(), is((AuthenticationSuccessHandler) withSuccessHandler));
    }

    private static Object extractFiledValue(Object object, String fieldName) throws IllegalAccessException {
        return extractFiledValue(object.getClass(), object, fieldName);
    }

    private static Object extractFiledValue(Class type, Object object, String fieldName) throws IllegalAccessException {
        if (Object.class.equals(type)) {
            return null;
        }

        try {
            final Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            return extractFiledValue(type.getSuperclass(), object, fieldName);
        }
    }
}