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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static java.lang.String.format;
import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaNumericString;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StormpathAuthenticationConfiguration.class)
@WebIntegrationTest
@DirtiesContext
public class ITSpringSecurityStormpath {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private MockStormpath mockStormpath;

    private String baseUrl;
    private WebTarget target;

    @Before
    public void setUp() {
        baseUrl = format("http://localhost:%d", port);
        final JerseyClient client = JerseyClientBuilder.createClient();
        client.property(ClientProperties.FOLLOW_REDIRECTS, false);
        target = client.target(baseUrl + "/stormpath");
    }

    @Test
    public void Can_sign_in_using_stormpath() throws IOException {

        final String username = "test";
        final String password = "Password1";
        final Form form = new Form();

        // Given
        form.param("username", username);
        form.param("password", password);
        mockStormpath.mockCurrentTenant(baseUrl);
        mockStormpath.mockTenant(baseUrl);
        mockStormpath.mockApplication(baseUrl);
        mockStormpath.mockLogin(baseUrl, username, password);
        mockStormpath.mockEmptyGroups(baseUrl);
        final Response forbidden = target.request().get();
        final Response signIn = target.path("signIn").request().post(form(form));
        final Cookie sessionCookie = signIn.getCookies().get("JSESSIONID");

        // When
        final Response annotation = target.request().cookie(sessionCookie).get();

        // Then
        mockStormpath.verifyCurrentTenant();
        mockStormpath.verifyTenant();
        mockStormpath.verifyApplication();
        mockStormpath.verifyLogin(username, password);
        mockStormpath.verifyEmptyGroups();
        assertThat(forbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(signIn.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.readEntity(String.class), is(username));
    }

    @Test
    public void Can_use_stormpath_groups_to_restrict_access_to_certain_paths() throws IOException {

        final Form role1Form = new Form();
        final Form role2Form = new Form();
        final String username1 = "role1";
        final String password1 = "Password1";
        final String username2 = "role2";
        final String password2 = "Password1";
        final String authenticationId1 = someAlphaNumericString(22);
        final String authenticationId2 = someAlphaNumericString(22);

        // Given
        role1Form.param("username", username1);
        role1Form.param("password", password1);
        role2Form.param("username", username2);
        role2Form.param("password", password2);
        mockStormpath.mockCurrentTenant(baseUrl);
        mockStormpath.mockTenant(baseUrl);
        mockStormpath.mockApplication(baseUrl);
        mockStormpath.mockLogin(baseUrl, username1, password1, authenticationId1);
        mockStormpath.mockLogin(baseUrl, username2, password2, authenticationId2);
        mockStormpath.mockGroups("role1", baseUrl, authenticationId1);
        mockStormpath.mockGroups("role2", baseUrl, authenticationId2);
        final Response forbidden = target.request().get();
        final Response role1Forbidden = target.path("one").request().get();
        final Response role2Forbidden = target.path("two").request().get();
        final Response role1SignIn = target.path("signIn").request().post(form(role1Form));
        final Cookie sessionCookie1 = role1SignIn.getCookies().get("JSESSIONID");
        final Response role2SignIn = target.path("signIn").request().post(form(role2Form));
        final Cookie sessionCookie2 = role2SignIn.getCookies().get("JSESSIONID");
        final Response normalForbidden = target.request().get();

        // When
        final Response role1Success = target.path("one").request().cookie(sessionCookie1).get();
        final Response role1Failure = target.path("two").request().cookie(sessionCookie1).get();
        final Response role2Success = target.path("two").request().cookie(sessionCookie2).get();
        final Response role2Failure = target.path("one").request().cookie(sessionCookie2).get();
        final Response normal = target.request().cookie(sessionCookie1).get();

        // Then
        mockStormpath.verifyCurrentTenant();
        mockStormpath.verifyTenant();
        mockStormpath.verifyApplication();
        mockStormpath.verifyLogin(username1, password1);
        mockStormpath.verifyLogin(username2, password2);
        mockStormpath.verifyGroups(authenticationId1);
        mockStormpath.verifyGroups(authenticationId2);
        assertThat(forbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(role1Forbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(role2Forbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(normalForbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(role1SignIn.getStatus(), is(OK.getStatusCode()));
        assertThat(role2SignIn.getStatus(), is(OK.getStatusCode()));
        assertThat(role1Success.getStatus(), is(OK.getStatusCode()));
        assertThat(role1Success.readEntity(String.class), is(username1));
        assertThat(role1Failure.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(role2Success.getStatus(), is(OK.getStatusCode()));
        assertThat(role2Success.readEntity(String.class), is(username2));
        assertThat(role2Failure.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(normal.getStatus(), is(OK.getStatusCode()));
        assertThat(normal.readEntity(String.class), is(username1));
    }
}
