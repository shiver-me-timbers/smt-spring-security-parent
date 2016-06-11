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
import org.springframework.security.web.FilterChainProxy;
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

public class SecurityFilterChainConfigurerTest {

    private FilterChainProxy filterChainProxy;
    private ChainConfigurer<Filter> configurer;

    @Before
    public void setUp() {
        filterChainProxy = mock(FilterChainProxy.class);
        configurer = new SecurityFilterChainConfigurer(filterChainProxy);
    }

    @Test
    public void Can_update_a_filter() {

        final SecurityFilterChain chain1 = mock(SecurityFilterChain.class);
        final SecurityFilterChain chain2 = mock(SecurityFilterChain.class);
        final SecurityFilterChain chain3 = mock(SecurityFilterChain.class);
        final FilterTwo filter1 = mock(FilterTwo.class);
        final FilterTwo filter2 = mock(FilterTwo.class);

        // Given
        given(filterChainProxy.getFilterChains()).willReturn(asList(chain1, chain2, chain3));
        given(chain1.getFilters()).willReturn(asList(mock(FilterOne.class), filter1, mock(FilterThree.class)));
        given(chain2.getFilters())
            .willReturn(asList(mock(FilterOne.class), mock(FilterOne.class), mock(FilterThree.class)));
        given(chain3.getFilters()).willReturn(asList(mock(FilterOne.class), mock(FilterThree.class), filter2));

        // When
        configurer.updateFilters(FilterTwo.class, new Updater<FilterTwo>() {
            @Override
            public void update(FilterTwo filter) {
                filter.update();
            }
        });

        // Then
        verify(filter1).update();
        verify(filter2).update();
    }

    @Test
    public void Can_add_a_filter_before_another() {

        final FilterTwo filterTwo = mock(FilterTwo.class);

        final FilterOne filterOne = mock(FilterOne.class);
        final FilterThree filterThree = mock(FilterThree.class);
        final FilterFour filterFour = mock(FilterFour.class);

        final SecurityFilterChain chain1 = mock(SecurityFilterChain.class);
        final SecurityFilterChain chain2 = mock(SecurityFilterChain.class);
        final SecurityFilterChain chain3 = mock(SecurityFilterChain.class);
        final List<Filter> filters1 = new ArrayList<>(asList(filterOne, filterThree, filterFour));
        final List<Filter> filters2 = new ArrayList<>(asList(filterOne, filterFour));
        final List<Filter> filters3 = new ArrayList<>(asList(filterThree, filterFour, filterOne, filterThree));

        // Given
        given(filterChainProxy.getFilterChains()).willReturn(asList(chain1, chain2, chain3));
        given(chain1.getFilters()).willReturn(filters1);
        given(chain2.getFilters()).willReturn(filters2);
        given(chain3.getFilters()).willReturn(filters3);

        // When
        configurer.addBefore(filterTwo, FilterThree.class);

        // Then
        assertThat(filters1, contains(filterOne, filterTwo, filterThree, filterFour));
        assertThat(filters2, contains(filterOne, filterFour));
        assertThat(filters3, contains(filterTwo, filterThree, filterFour, filterOne, filterThree));
    }

    private interface FilterOne extends Filter {
    }

    private interface FilterTwo extends Filter {
        void update();
    }

    private interface FilterThree extends Filter {
    }

    private interface FilterFour extends Filter {
    }
}