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

import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import shiver.me.timbers.spring.security.JwtLogoutHandler;
import shiver.me.timbers.spring.security.fields.FieldMutator;
import shiver.me.timbers.spring.security.fields.Updater;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Karl Bennett
 */
public class JwtLogoutHandlerAdder implements LogoutHandlerAdder {

    private final FieldMutator mutator;
    private final JwtLogoutHandler logoutHandler;

    public JwtLogoutHandlerAdder(FieldMutator mutator, JwtLogoutHandler logoutHandler) {
        this.mutator = mutator;
        this.logoutHandler = logoutHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void modify(final LogoutFilter filter) {
        mutator.update(filter, "handlers", List.class, new Updater<List>() {
            @Override
            public List update(List oldHandlers) {
                final List<LogoutHandler> handlers = new ArrayList<>(oldHandlers);
                handlers.add(0, logoutHandler);
                return asList(handlers.toArray(new LogoutHandler[handlers.size()]));
            }
        });
    }
}
