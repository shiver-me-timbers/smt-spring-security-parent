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

import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.Filter;
import java.util.List;

/**
 * @author Karl Bennett
 */
public class SecurityFilterChainConfigurer implements ChainConfigurer<Filter> {

    private final FilterChainProxy filterChainProxy;

    public SecurityFilterChainConfigurer(FilterChainProxy filterChainProxy) {
        this.filterChainProxy = filterChainProxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends Filter> void updateFilters(Class<F> filterType, Updater<F> updater) {
        for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
            for (Filter filter : filterChain.getFilters()) {
                if (filterType.isAssignableFrom(filter.getClass())) {
                    updater.update((F) filter);
                }
            }
        }
    }

    @Override
    public void addBefore(Filter filter, Class<? extends Filter> filterClass) {
        for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
            final List<Filter> filters = filterChain.getFilters();
            final int index = findFirstIndexOf(filterClass, filters);
            if (index >= 0) {
                filters.add(index, filter);
            }
        }
    }

    private static int findFirstIndexOf(Class<? extends Filter> filterClass, List<Filter> filters) {
        for (int i = 0; i < filters.size(); i++) {
            if (filterClass.isAssignableFrom(filters.get(i).getClass())) {
                return i;
            }
        }
        return -1;
    }
}
