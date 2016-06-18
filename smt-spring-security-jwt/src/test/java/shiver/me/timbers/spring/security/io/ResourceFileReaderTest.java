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

package shiver.me.timbers.spring.security.io;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.spring.security.TestFiles.readFile;

public class ResourceFileReaderTest {

    @Test
    public void Can_read_a_file_from_the_class_path() throws IOException {

        // Given
        final String fileName = "test.txt";
        final String expected = readFile(fileName);

        // When
        final String actual = new ResourceFileReader().read(fileName);

        // Then
        assertThat(actual, equalTo(expected));
    }
}