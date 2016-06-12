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

import javax.servlet.http.Cookie;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.somePositiveInteger;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaNumericString;

public class CookieBakeryTest {

    @Test
    public void Can_create_a_cookie() {

        // Given
        final int maxAgeDuration = somePositiveInteger();
        final TimeUnit maxAgeUnit = someEnum(TimeUnit.class);
        final boolean secure = someBoolean();
        final boolean httpOnly = someBoolean();
        final String name = someAlphaNumericString(5);
        final String value = someAlphaNumericString(8);

        // When
        final Cookie actual = new CookieBakery(maxAgeDuration, maxAgeUnit, secure, httpOnly).bake(name, value);

        // Then
        assertThat(actual.getName(), is(name));
        assertThat(actual.getValue(), is(value));
        assertThat(actual.getMaxAge(), is((int) maxAgeUnit.toSeconds(maxAgeDuration)));
        assertThat(actual.getDomain(), nullValue());
        assertThat(actual.getPath(), nullValue());
        assertThat(actual.getComment(), nullValue());
        assertThat(actual.getSecure(), is(secure));
        assertThat(actual.isHttpOnly(), is(httpOnly));
    }

    @Test
    public void Can_create_a_session_cookie() {

        // Given
        final int maxAgeDuration = -1;
        final boolean secure = someBoolean();
        final boolean httpOnly = someBoolean();
        final String name = someAlphaNumericString(5);
        final String value = someAlphaNumericString(8);

        // When
        final Cookie actual = new CookieBakery(maxAgeDuration, someEnum(TimeUnit.class), secure, httpOnly)
            .bake(name, value);

        // Then
        assertThat(actual.getName(), is(name));
        assertThat(actual.getValue(), is(value));
        assertThat(actual.getMaxAge(), is(-1));
        assertThat(actual.getDomain(), nullValue());
        assertThat(actual.getPath(), nullValue());
        assertThat(actual.getComment(), nullValue());
        assertThat(actual.getSecure(), is(secure));
        assertThat(actual.isHttpOnly(), is(httpOnly));
    }
}