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

import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class ReflectionFieldSetterTest {

    @Test
    public void Can_set_a_field() throws NoSuchFieldException, IllegalAccessException {

        // Given
        final FieldTest object = new FieldTest();
        final Field field = object.getClass().getDeclaredField("test");
        final String expected = someString();

        // When
        new ReflectionFieldSetter().set(object, field, expected);

        // Then
        assertThat(object.test, is(expected));
    }

    private static class FieldTest {
        private final String test = someString();
    }
}