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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;

/**
 * @author Karl Bennett
 */
public class StormpathApplicationFactory implements ApplicationFactory {

    private final ApplicationCriteriaFactory applicationCriteriaFactory;

    public StormpathApplicationFactory(ApplicationCriteriaFactory applicationCriteriaFactory) {
        this.applicationCriteriaFactory = applicationCriteriaFactory;
    }

    @Override
    public Application create(Client client, String applicationHref, String applicationName) {
        if (applicationName.isEmpty() && applicationHref.isEmpty()) {
            throw new IllegalArgumentException(
                "Either one of (smt.spring.security.stormpath.application.name) or " +
                    "(smt.spring.security.stormpath.application.href) must be set."
            );
        }

        if (!applicationHref.isEmpty()) {
            return client.getResource(applicationHref, Application.class);
        }

        return client.getCurrentTenant()
            .getApplications(
                applicationCriteriaFactory.where(applicationCriteriaFactory.name().eqIgnoreCase(applicationName))
            ).iterator().next();
    }
}
