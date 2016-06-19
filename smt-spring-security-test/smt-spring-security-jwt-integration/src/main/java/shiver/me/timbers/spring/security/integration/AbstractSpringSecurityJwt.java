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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.client.Entity.text;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.spring.security.integration.SpringSecurityJwtController.TEXT;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JwtAnnotationSecurityConfiguration.class)
@WebIntegrationTest
@DirtiesContext
public abstract class AbstractSpringSecurityJwt {

    @Value("${local.server.port}")
    private int port;

    @Value("${smt.spring.security.jwt.tokenName}")
    private String tokenName;

    private WebTarget annotationTarget;
    private WebTarget normalTarget;

    @Before
    public void setUp() {
        final JerseyClient client = JerseyClientBuilder.createClient();
        client.property(ClientProperties.FOLLOW_REDIRECTS, false);
        annotationTarget = client.target(format("http://localhost:%d/jwt", port));
        normalTarget = client.target(format("http://localhost:%d/normal", port));
    }

    @Test
    public void Can_login_to_all_security_contexts_with_jwt_cookie() {

        final Form form = new Form();

        // Given
        form.param("username", "user");
        form.param("password", "password");
        final Response annotationForbidden = annotationTarget.request().get();
        final Response normalForbidden = normalTarget.request().get();
        final Response signIn = annotationTarget.path("signIn").request().post(form(form));

        // When
        final Response annotation = annotationTarget.request().cookie(signIn.getCookies().get(tokenName)).get();
        final Response normal = normalTarget.request().cookie(signIn.getCookies().get(tokenName)).get();

        // Then
        assertThat(annotationForbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(normalForbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(signIn.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.readEntity(String.class), is(TEXT));
        assertThat(normal.getStatus(), is(OK.getStatusCode()));
        assertThat(normal.readEntity(String.class), is(TEXT));
    }

    @Test
    public void Can_login_to_all_security_contexts_with_jwt_header() {

        final Form form = new Form();

        // Given
        form.param("username", "user");
        form.param("password", "password");
        final Response annotationForbidden = annotationTarget.request().get();
        final Response normalForbidden = annotationTarget.request().get();
        final Response signIn = annotationTarget.path("signIn").request().post(form(form));

        // When
        final Response annotation = annotationTarget.request().header(tokenName, signIn.getHeaderString(tokenName)).get();
        final Response normal = normalTarget.request().header(tokenName, signIn.getHeaderString(tokenName)).get();

        // Then
        assertThat(annotationForbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(normalForbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(signIn.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.readEntity(String.class), is(TEXT));
        assertThat(normal.getStatus(), is(OK.getStatusCode()));
        assertThat(normal.readEntity(String.class), is(TEXT));
    }

    @Test
    public void Can_logout_with_jwt_cookie() {

        final Form form = new Form();

        // Given
        form.param("username", "user");
        form.param("password", "password");
        final Response signIn = annotationTarget.path("signIn").request().post(form(form));

        // When
        final Response annotation = annotationTarget.path("signOut").request().cookie(signIn.getCookies().get(tokenName))
            .post(text(null));
        final Response normal = normalTarget.path("signOut").request().cookie(signIn.getCookies().get(tokenName))
            .post(text(null));

        // Then
        assertThat(signIn.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.getCookies().get(tokenName).getMaxAge(), is(-1));
        assertThat(annotation.getCookies().get(tokenName).getValue(), isEmptyString());
        assertThat(normal.getStatus(), is(OK.getStatusCode()));
        assertThat(normal.getCookies().get(tokenName).getMaxAge(), is(-1));
        assertThat(normal.getCookies().get(tokenName).getValue(), isEmptyString());
    }
}