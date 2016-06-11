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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomDoubles.someDouble;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class ReflectionFieldMutatorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private FieldFinder fieldFinder;
    private FieldGetter fieldGetter;
    private FieldSetter fieldSetter;
    private FieldMutator mutator;

    @Before
    public void setUp() {
        fieldGetter = mock(FieldGetter.class);
        fieldSetter = mock(FieldSetter.class);
        fieldFinder = mock(FieldFinder.class);
        mutator = new ReflectionFieldMutator(fieldFinder, fieldGetter, fieldSetter);
    }

    @Test
    public void Can_retrieve_a_field() throws NoSuchFieldException, IllegalAccessException {

        final Object object = someObject();
        final String name = someString();
        final Class type = someClass();

        final Field field = someField();

        final Object expected = someObject();

        // Given
        given(fieldFinder.findField(object, name, type)).willReturn(field);
        given(fieldGetter.get(object, field)).willReturn(expected);

        // When
        final Object actual = mutator.retrieve(object, name, type);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_to_find_a_field_to_retrieve() throws NoSuchFieldException {

        final Object object = someObject();
        final String name = someString();
        final Class type = someClass();

        final NoSuchFieldException exception = new NoSuchFieldException();

        // Given
        given(fieldFinder.findField(object, name, type)).willThrow(exception);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectCause(is(exception));

        // When
        mutator.retrieve(object, name, type);
    }

    @Test
    public void Can_fail_to_get_a_field_to_retrieve() throws NoSuchFieldException, IllegalAccessException {

        final Object object = someObject();
        final String name = someString();
        final Class type = someClass();

        final Field field = someField();

        final IllegalAccessException exception = new IllegalAccessException();

        // Given
        given(fieldFinder.findField(object, name, type)).willReturn(field);
        given(fieldGetter.get(object, field)).willThrow(exception);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectCause(is(exception));

        // When
        mutator.retrieve(object, name, type);
    }

    @Test
    public void Can_update_a_field() throws NoSuchFieldException, IllegalAccessException {

        final Object object = someObject();
        final String name = someString();
        final Class type = someClass();
        final Object value = someObject();

        final Field field = someField();

        // Given
        given(fieldFinder.findField(object, name, type)).willReturn(field);

        // When
        mutator.update(object, name, type, value);

        // Then
        verify(fieldSetter).set(object, field, value);
    }

    @Test
    public void Can_fail_to_find_a_field_to_update() throws NoSuchFieldException {

        final Object object = someObject();
        final String name = someString();
        final Class type = someClass();
        final Object value = someObject();

        final NoSuchFieldException exception = new NoSuchFieldException();

        // Given
        given(fieldFinder.findField(object, name, type)).willThrow(exception);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectCause(is(exception));

        // When
        mutator.update(object, name, type, value);
    }

    @Test
    public void Can_fail_to_set_a_field_to_update() throws NoSuchFieldException, IllegalAccessException {

        final Object object = someObject();
        final String name = someString();
        final Class type = someClass();
        final Object value = someObject();

        final Field field = someField();

        final IllegalAccessException exception = new IllegalAccessException();

        // Given
        given(fieldFinder.findField(object, name, type)).willReturn(field);
        willThrow(exception).given(fieldSetter).set(object, field, value);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectCause(is(exception));

        // When
        mutator.update(object, name, type, value);
    }

    private static Object someObject() {
        return someThing(someInteger(), someDouble(), someString());
    }

    private static Field someField() {
        try {
            return FieldTest.class.getDeclaredField(someThing("one", "two", "three", "four"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class someClass() {
        return someThing(Integer.class, Double.class, String.class);
    }

    private static class FieldTest {
        private final int one = someInteger();
        private final String two = someString();
        private final double three = someDouble();
        private final String four = someString();
    }
}