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

import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.StringExpressionFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.matchers.Matchers.hasFieldThat;

public class StormpathApplicationCriteriaFactoryTest {

    private StormpathApplicationCriteriaFactory factory;

    @Before
    public void setUp() {
        factory = new StormpathApplicationCriteriaFactory();
    }

    @Test
    public void Can_create_a_name_expression() {

        // When
        final StringExpressionFactory actual = factory.name();

        // Then
        assertThat(actual, not(nullValue()));
    }

    @Test
    public void Can_create_a_where_criteria() {

        // Then
        final Criterion criterion = mock(Criterion.class);

        // When
        final ApplicationCriteria actual = factory.where(criterion);

        // Then
        assertThat(actual, hasFieldThat("criterionEntries", contains(criterion)));
    }
}