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

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Karl Bennett
 */
public class WrappedUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtAuthenticationSuccessHandler jwtSuccessHandler;

    public WrappedUsernamePasswordAuthenticationFilter(
        UsernamePasswordAuthenticationFilter filter,
        JwtAuthenticationSuccessHandler jwtSuccessHandler
    ) {
        copyFields(filter, this);
        super.setAuthenticationSuccessHandler(jwtSuccessHandler.withDelegate(super.getSuccessHandler()));
        this.jwtSuccessHandler = jwtSuccessHandler;
    }

    private static void copyFields(Object from, Object to) {
        copyFields(from.getClass(), from, to);
    }

    private static void copyFields(Class type, Object from, Object to) {
        if (Object.class.equals(type)) {
            return;
        }

        for (Field field : type.getDeclaredFields()) {
            copyField(field, from, to);
        }

        copyFields(type.getSuperclass(), from, to);
    }

    private static void copyField(Field field, Object from, Object to) {
        if (Modifier.isStatic(field.getModifiers())) {
            return;
        }

        field.setAccessible(true);
        try {
            field.set(to, field.get(from));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public AuthenticationSuccessHandler getSuccessHandler() {
        return super.getSuccessHandler();
    }

    @Override
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        jwtSuccessHandler.withDelegate(successHandler);
    }
}
