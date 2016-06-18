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

package shiver.me.timbers.spring.security.time;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.matchers.Matchers.fallsOn;
import static shiver.me.timbers.matchers.Within.within;

public class DateClockTest {

    @Test
    public void Can_get_now_plus_some_time() {

        // Given
        final Long duration = someLongBetween(0L, 100L);
        final TimeUnit unit = someEnum(TimeUnit.class);

        // When
        final Date actual = new DateClock().nowPlus(duration, unit);

        // Then
        assertThat(actual, fallsOn(new Date(new Date().getTime() + unit.toMicros(duration)), within(1L, SECONDS)));
    }
}