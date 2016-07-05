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

package shiver.me.timbers.spring.security.secret;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import shiver.me.timbers.spring.security.io.FileReader;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class ChoosingSecretKeeperTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void Can_choose_a_secret_value() {

        // Given
        final String expected = someString();

        // When
        final String actual = new ChoosingSecretKeeper(expected, "", mock(FileReader.class)).getSecret();

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_choose_a_secret_file() throws IOException {

        final String filePath = someString();
        final FileReader fileReader = mock(FileReader.class);

        final String expected = someString();

        // Given
        given(fileReader.read(filePath)).willReturn(expected);

        // When
        final String actual = new ChoosingSecretKeeper("", filePath, fileReader).getSecret();

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_to_read_a_secret_file() throws IOException {

        final String filePath = someString();
        final FileReader fileReader = mock(FileReader.class);

        final IOException exception = new IOException();

        // Given
        given(fileReader.read(filePath)).willThrow(exception);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectCause(is(exception));

        // When
        new ChoosingSecretKeeper("", filePath, fileReader).getSecret();
    }

    @Test
    public void Can_make_sure_at_least_one_secret_is_set() {

        // Given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
            "Either one of (smt.spring.security.jwt.secret) or (smt.spring.security.jwt.secretFile) must be set."
        );

        // When
        new ChoosingSecretKeeper("", "", mock(FileReader.class));
    }
}