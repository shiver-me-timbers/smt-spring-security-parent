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

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class FilterChainProxyWeaverTest {

    private FilterChainProxy filterChainProxy;
    private ChainWeaver<SecurityFilterChain> chainWeaver;
    private Weaver proxyWeaver;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        filterChainProxy = mock(FilterChainProxy.class);
        chainWeaver = mock(ChainWeaver.class);
        proxyWeaver = new FilterChainProxyWeaver(filterChainProxy, chainWeaver);
    }

    @Test
    public void Can_weave_a_filter_chain_proxy() {

        final SecurityFilterChain chain1 = mock(SecurityFilterChain.class);
        final SecurityFilterChain chain2 = mock(SecurityFilterChain.class);
        final SecurityFilterChain chain3 = mock(SecurityFilterChain.class);

        // Given
        given(filterChainProxy.getFilterChains()).willReturn(asList(chain1, chain2, chain3));

        // When
        proxyWeaver.weave();

        // Then
        verify(chainWeaver).weave(chain1);
        verify(chainWeaver).weave(chain2);
        verify(chainWeaver).weave(chain3);
        verifyNoMoreInteractions(chainWeaver);
    }
}