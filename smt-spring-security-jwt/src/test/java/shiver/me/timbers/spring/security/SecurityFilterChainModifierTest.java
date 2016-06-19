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
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SecurityFilterChainModifierTest {

    private ChainModifier<SecurityFilterChain, Filter> configurer;

    @Before
    public void setUp() {
        configurer = new SecurityFilterChainModifier();
    }

    @Test
    public void Can_modify_a_filter() {

        @SuppressWarnings("unchecked")
        final Modifier<FilterTwo> modifier = mock(Modifier.class);

        final SecurityFilterChain chain = mock(SecurityFilterChain.class);
        final FilterTwo filter1 = mock(FilterTwo.class);

        // Given
        given(chain.getFilters()).willReturn(asList(mock(FilterOne.class), filter1, mock(FilterThree.class)));

        // When
        configurer.modifyLink(chain, FilterTwo.class, modifier);

        // Then
        verify(modifier).modify(filter1);
    }

    @Test
    public void Can_add_a_filter_before_another() {

        final FilterTwo filterTwo = mock(FilterTwo.class);

        final FilterOne filterOne = mock(FilterOne.class);
        final FilterThree filterThree = mock(FilterThree.class);
        final FilterFour filterFour = mock(FilterFour.class);

        final SecurityFilterChain chain = mock(SecurityFilterChain.class);
        final List<Filter> filters = new ArrayList<>(asList(filterOne, filterThree, filterFour, filterThree));

        // Given
        given(chain.getFilters()).willReturn(filters);

        // When
        configurer.addBefore(chain, filterTwo, FilterThree.class);

        // Then
        assertThat(filters, contains(filterOne, filterTwo, filterThree, filterFour, filterThree));
    }

    @Test
    public void Can_not_add_a_filter_before_a_filter_that_does_not_exist() {

        final FilterTwo filterTwo = mock(FilterTwo.class);

        final FilterOne filterOne = mock(FilterOne.class);
        final FilterThree filterThree = mock(FilterThree.class);
        final FilterFour filterFour = mock(FilterFour.class);

        final SecurityFilterChain chain = mock(SecurityFilterChain.class);
        final List<Filter> filters = new ArrayList<>(asList(filterOne, filterFour));

        // Given
        given(chain.getFilters()).willReturn(filters);

        // When
        configurer.addBefore(chain, filterTwo, FilterThree.class);

        // Then
        assertThat(filters, contains(filterOne, filterFour));
    }

    private interface FilterOne extends Filter {
    }

    private interface FilterTwo extends Filter {
    }

    private interface FilterThree extends Filter {
    }

    private interface FilterFour extends Filter {
    }
}