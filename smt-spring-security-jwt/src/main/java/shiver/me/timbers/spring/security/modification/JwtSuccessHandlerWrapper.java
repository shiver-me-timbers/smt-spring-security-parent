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

package shiver.me.timbers.spring.security.modification;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shiver.me.timbers.spring.security.JwtAuthenticationSuccessHandler;
import shiver.me.timbers.spring.security.fields.FieldMutator;

/**
 * @author Karl Bennett
 */
public class JwtSuccessHandlerWrapper implements SuccessHandlerWrapper {

    private final FieldMutator mutator;
    private final JwtAuthenticationSuccessHandler successHandler;

    public JwtSuccessHandlerWrapper(FieldMutator mutator, JwtAuthenticationSuccessHandler successHandler) {
        this.mutator = mutator;
        this.successHandler = successHandler;
    }

    @Override
    public void modify(final UsernamePasswordAuthenticationFilter filter) {
        filter.setAuthenticationSuccessHandler(
            successHandler.withDelegate(
                mutator.retrieve(filter, "successHandler", AuthenticationSuccessHandler.class)
            )
        );
    }

}
