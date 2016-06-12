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

    private int maxAgeDuration;
    private TimeUnit maxAgeUnit;
    private boolean secure;
    private boolean httpOnly;
    private String domain;
    private String path;
    private Bakery<Cookie> bakery;

    @Before
    public void setUp() {
        maxAgeDuration = somePositiveInteger();
        maxAgeUnit = someEnum(TimeUnit.class);
        domain = someAlphaNumericString(5);
        path = someAlphaNumericString(8);
        secure = someBoolean();
        httpOnly = someBoolean();
        bakery = new CookieBakery(maxAgeDuration, maxAgeUnit, domain, path, secure, httpOnly);
    }

    @Test
    public void Can_create_a_cookie() {

        // Given
        final String name = someAlphaNumericString(13);
        final String value = someAlphaNumericString(21);

        // When
        final Cookie actual = bakery.bake(name, value);

        // Then
        assertThat(actual.getName(), is(name));
        assertThat(actual.getValue(), is(value));
        assertThat(actual.getMaxAge(), is((int) maxAgeUnit.toSeconds(maxAgeDuration)));
        assertThat(actual.getDomain(), is(domain.toLowerCase()));
        assertThat(actual.getPath(), is(path));
        assertThat(actual.getComment(), nullValue());
        assertThat(actual.getSecure(), is(secure));
        assertThat(actual.isHttpOnly(), is(httpOnly));
    }

    @Test
    public void Can_create_a_cookie_with_an_empty_domain() {

        // Given
        final String name = someAlphaNumericString(13);
        final String value = someAlphaNumericString(21);

        // When
        final Cookie actual = new CookieBakery(maxAgeDuration, maxAgeUnit, "", path, secure, httpOnly).bake(name, value);

        // Then
        assertThat(actual.getName(), is(name));
        assertThat(actual.getValue(), is(value));
        assertThat(actual.getMaxAge(), is((int) maxAgeUnit.toSeconds(maxAgeDuration)));
        assertThat(actual.getDomain(), nullValue());
        assertThat(actual.getPath(), is(path));
        assertThat(actual.getComment(), nullValue());
        assertThat(actual.getSecure(), is(secure));
        assertThat(actual.isHttpOnly(), is(httpOnly));
    }

    @Test
    public void Can_create_a_cookie_with_no_domain() {

        // Given
        final String name = someAlphaNumericString(13);
        final String value = someAlphaNumericString(21);

        // When
        final Cookie actual = new CookieBakery(maxAgeDuration, maxAgeUnit, null, path, secure, httpOnly)
            .bake(name, value);

        // Then
        assertThat(actual.getName(), is(name));
        assertThat(actual.getValue(), is(value));
        assertThat(actual.getMaxAge(), is((int) maxAgeUnit.toSeconds(maxAgeDuration)));
        assertThat(actual.getDomain(), nullValue());
        assertThat(actual.getPath(), is(path));
        assertThat(actual.getComment(), nullValue());
        assertThat(actual.getSecure(), is(secure));
        assertThat(actual.isHttpOnly(), is(httpOnly));
    }

    @Test
    public void Can_create_a_session_cookie() {

        // Given
        final int maxAgeDuration = -1;
        final String name = someAlphaNumericString(13);
        final String value = someAlphaNumericString(21);

        // When
        final Cookie actual = new CookieBakery(maxAgeDuration, maxAgeUnit, domain, path, secure, httpOnly)
            .bake(name, value);

        // Then
        assertThat(actual.getName(), is(name));
        assertThat(actual.getValue(), is(value));
        assertThat(actual.getMaxAge(), is(-1));
        assertThat(actual.getDomain(), is(domain.toLowerCase()));
        assertThat(actual.getPath(), is(path));
        assertThat(actual.getComment(), nullValue());
        assertThat(actual.getSecure(), is(secure));
        assertThat(actual.isHttpOnly(), is(httpOnly));
    }
}