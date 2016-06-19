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

package shiver.me.timbers.spring.security.weaving;

import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Karl Bennett
 */
public class FilterChainProxyWeaver implements Weaver {

    private final FilterChainProxy filterChainProxy;
    private final ChainWeaver<SecurityFilterChain> chainWeaver;

    public FilterChainProxyWeaver(FilterChainProxy filterChainProxy, ChainWeaver<SecurityFilterChain> chainWeaver) {
        this.filterChainProxy = filterChainProxy;
        this.chainWeaver = chainWeaver;
    }

    @Override
    public void weave() {
        for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
            chainWeaver.weave(filterChain);
        }
    }
}