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
import org.mockito.Matchers;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomDoubles.someDouble;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class FieldExtractorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    private FieldGetter fieldGetter;
    private FieldExtractor extractor;

    @Before
    public void setUp() {
        fieldGetter = mock(FieldGetter.class);
        extractor = new FieldExtractor(fieldGetter);
    }

    @Test
    public void Can_extract_a_field() throws IllegalAccessException {

        final FieldTest object = new FieldTest(someString());

        final String expected = someString();

        // Given
        given(fieldGetter.get(any(Field.class), eq(object))).willReturn(expected);

        // When
        final String actual = extractor.extract(String.class, "four", object);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_extract_a_field_from_a_parent_class() throws IllegalAccessException {

        final FieldTest object = new FieldTest(someInteger(), someString());

        final Integer expected = someInteger();

        // Given
        given(fieldGetter.get(any(Field.class), eq(object))).willReturn(expected);

        // When
        final Integer actual = extractor.extract(int.class, "zero", object);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_extract_a_field() throws IllegalAccessException {

        final FieldTest object = new FieldTest(someString());

        final IllegalAccessException exception = new IllegalAccessException();

        // Given
        given(fieldGetter.get(any(Field.class), eq(object))).willThrow(exception);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectCause(is(exception));

        // When
        extractor.extract(String.class, "four", object);
    }

    @Test
    public void Can_extract_no_field_with_the_wrong_type() throws IllegalAccessException {

        // Given
        final FieldTest object = new FieldTest(someString());

        // When
        final Integer actual = extractor.extract(int.class, "four", object);

        // Then
        verify(fieldGetter, never()).get(any(Field.class), Matchers.anyObject());
        assertThat(actual, nullValue());
    }

    @Test
    public void Can_extract_no_field_with_the_wrong_name() throws IllegalAccessException {

        // Given
        final FieldTest object = new FieldTest(someString());

        // When
        final String actual = extractor.extract(String.class, "five", object);

        // Then
        verify(fieldGetter, never()).get(any(Field.class), Matchers.anyObject());
        assertThat(actual, nullValue());
    }

    private static class ParentTest {

        private final int zero;

        private ParentTest(int zero) {
            this.zero = zero;
        }
    }

    private static class FieldTest extends ParentTest {

        private final int one = someInteger();
        private final String two = someString();
        private final double three = someDouble();
        private final String four;

        private FieldTest(String four) {
            this(someInteger(), four);
        }

        private FieldTest(int zero, String four) {
            super(zero);
            this.four = four;
        }
    }
}