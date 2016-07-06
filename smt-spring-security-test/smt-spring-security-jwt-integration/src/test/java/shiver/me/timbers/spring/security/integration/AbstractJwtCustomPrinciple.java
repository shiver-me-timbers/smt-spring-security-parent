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

package shiver.me.timbers.spring.security.integration;


import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import shiver.me.timbers.spring.security.jwt.AuthenticationConverter;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.matchers.Matchers.hasField;
import static shiver.me.timbers.matchers.Matchers.hasProperty;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest
@DirtiesContext
public abstract class AbstractJwtCustomPrinciple {

    @Value("${local.server.port}")
    private int port;

    @Value("${smt.spring.security.jwt.tokenName}")
    private String tokenName;

    @Autowired
    private AuthenticationConverter<CustomPrincipal> authenticationConverter;

    private WebTarget annotationTarget;

    @Before
    public void setUp() {
        final JerseyClient client = JerseyClientBuilder.createClient();
        client.property(ClientProperties.FOLLOW_REDIRECTS, false);
        annotationTarget = client.target(format("http://localhost:%d/custom", port));
    }

    @After
    @SuppressWarnings("unchecked")
    public void tearDown() {
        reset(authenticationConverter);
    }

    @Test
    public void Can_sign_in_using_a_custom_principle() {

        final Form form = new Form();

        // Given
        form.param("username", "user");
        form.param("password", "password");
        final Response annotationForbidden = annotationTarget.request().get();
        final Response signIn = annotationTarget.path("signIn").request().post(form(form));

        // When
        final Response annotation = annotationTarget.request().cookie(signIn.getCookies().get(tokenName)).get();

        // Then
        verify(authenticationConverter).convert((Authentication) argThat(hasProperty("principal.username", "user")));
        verify(authenticationConverter).convert((CustomPrincipal) argThat(hasField("username", "user")));
        assertThat(annotationForbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(signIn.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.readEntity(String.class), is("user"));
    }
}
