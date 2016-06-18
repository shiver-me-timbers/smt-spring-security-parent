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

package shiver.me.timbers.spring.security.fields;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomDoubles.someDouble;
import static shiver.me.timbers.data.random.RandomFloats.someFloat;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class ReflectionFieldFinderTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private ReflectionFieldFinder finder;

    @Before
    public void setUp() {
        finder = new ReflectionFieldFinder();
    }

    @Test
    public void Can_find_a_field() throws NoSuchFieldException {

        // Given
        final FieldTest object = new FieldTest();

        // When
        final Field actual = finder.findField(object, "four", String.class);

        // Then
        assertThat(actual.getName(), is("four"));
        assertThat(actual.getType(), equalTo((Class) String.class));
    }

    @Test
    public void Can_find_a_field_from_a_parent_class() throws NoSuchFieldException {

        // Given
        final FieldTest object = new FieldTest();

        // When
        final Field actual = finder.findField(object, "zero", int.class);

        // Then
        assertThat(actual.getName(), is("zero"));
        assertThat(actual.getType(), equalTo((Class) int.class));
    }

    @Test
    public void Can_fail_to_find_a_field_with_the_wrong_type() throws NoSuchFieldException {

        // Given
        final FieldTest object = new FieldTest();
        final String name = "four";
        final Class<Integer> type = int.class;
        expectedException.expect(NoSuchFieldException.class);
        expectedException.expectMessage(
            format("Could not find a field with name (%s) and type (%s).", name, type.getName())
        );

        // When
        finder.findField(object, name, type);
    }

    @Test
    public void Can_fail_to_find_a_field_with_the_wrong_name() throws NoSuchFieldException {

        // Given
        final FieldTest object = new FieldTest();
        final String name = "five";
        final Class<String> type = String.class;
        expectedException.expect(NoSuchFieldException.class);
        expectedException.expectMessage(
            format("Could not find a field with name (%s) and type (%s).", name, type.getName())
        );

        // When
        finder.findField(object, name, type);
    }

    private static class ParentTest {
        private final int zero = someInteger();
    }

    private static class FieldTest extends ParentTest {
        private final float one = someFloat();
        private final String two = someString();
        private final double three = someDouble();
        private final String four = someString();
    }
}