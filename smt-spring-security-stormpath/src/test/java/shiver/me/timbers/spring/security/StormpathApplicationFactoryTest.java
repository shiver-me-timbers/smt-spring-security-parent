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
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.StringExpressionFactory;
import com.stormpath.sdk.tenant.Tenant;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class StormpathApplicationFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ApplicationCriteriaFactory applicationCriteriaFactory;
    private Client client;
    private StormpathApplicationFactory factory;

    @Before
    public void setUp() {
        applicationCriteriaFactory = mock(ApplicationCriteriaFactory.class);
        client = mock(Client.class);
        factory = new StormpathApplicationFactory(applicationCriteriaFactory);
    }

    @Test
    public void Can_create_an_application_from_an_application_href() {

        final String applicationHref = someString();

        final Application expected = mock(Application.class);

        // Given
        given(client.getResource(applicationHref, Application.class)).willReturn(expected);

        // When
        final Application actual = factory.create(client, applicationHref, "");

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_create_an_application_from_an_application_name() {

        final String applicationName = someString();

        final Application expected = mock(Application.class);

        final Tenant tenant = mock(Tenant.class);
        final StringExpressionFactory name = mock(StringExpressionFactory.class);
        final Criterion eqApplicationName = mock(Criterion.class);
        final ApplicationCriteria where = mock(ApplicationCriteria.class);
        final ApplicationList applications = mock(ApplicationList.class);

        // Given
        given(client.getCurrentTenant()).willReturn(tenant);
        given(applicationCriteriaFactory.name()).willReturn(name);
        given(name.eqIgnoreCase(applicationName)).willReturn(eqApplicationName);
        given(applicationCriteriaFactory.where(eqApplicationName)).willReturn(where);
        given(tenant.getApplications(where)).willReturn(applications);
        given(applications.iterator()).willReturn(singletonList(expected).iterator());

        // When
        final Application actual = factory.create(client, "", applicationName);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Cannot_create_an_application_with_no_application_href_or_name() {

        // Given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
            "Either one of (smt.spring.security.stormpath.application.name) or " +
                "(smt.spring.security.stormpath.application.href) must be set."
        );

        // When
        factory.create(client, "", "");
    }
}