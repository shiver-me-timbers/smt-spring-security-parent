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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.spring.security.integration.SpringSecurityJwtController.TEXT;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringSecurityJwtIntegrationConfiguration.class)
@WebIntegrationTest
public class ITSpringSecuirtyJwt {

    @Value("${local.server.port}")
    private int port;

    @Value("${smt.spring.security.jwt.token.name}")
    private String tokenName;

    private WebTarget target;

    @Before
    public void setUp() {
        final JerseyClient client = JerseyClientBuilder.createClient();
        client.property(ClientProperties.FOLLOW_REDIRECTS, false);
        target = client.target(format("http://localhost:%d/", port));
    }

    @Test
    public void Can_login_with_jwt() {

        final Form form = new Form();

        // Given
        form.param("username", "user");
        form.param("password", "password");
        final Response forbidden = target.request().get();
        final Response signIn = target.path("signIn").request().post(form(form));

        // When
        final Response actual = target.request().cookie(signIn.getCookies().get(tokenName)).get();

        // Then
        assertThat(forbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(signIn.getStatus(), is(OK.getStatusCode()));
        assertThat(actual.getStatus(), is(OK.getStatusCode()));
        assertThat(actual.readEntity(String.class), is(TEXT));
    }
}
