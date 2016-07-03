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


import org.apache.commons.io.IOUtils;
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
import shiver.me.timbers.http.mock.HttpMockHandler;
import shiver.me.timbers.http.mock.HttpMockResponse;
import shiver.me.timbers.http.mock.HttpMockServer;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.http.mock.HttpMock.h;
import static shiver.me.timbers.http.mock.HttpMock.headers;
import static shiver.me.timbers.spring.security.integration.SpringSecurityController.TEXT;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StormpathAuthenticationConfiguration.class)
@WebIntegrationTest
@DirtiesContext
public class ITSpringSecurityStormpath {

    @Value("${local.server.port}")
    private int port;

    @Value("${smt.spring.security.stormpath.applicationName}")
    private String applicationName;

    @Autowired
    private HttpMockServer http;

    private String baseUrl;
    private WebTarget annotationTarget;

    @Before
    public void setUp() {
        http.ignoreHeaders(
            "Host",
            "Accept-Encoding",
            "Accept",
            "User-Agent",
            "X-Stormpath-Date",
            "Authorization",
            "Connection",
            "Content-Length",
            "Content-Type"
        );
        baseUrl = format("http://localhost:%d", port);
        final JerseyClient client = JerseyClientBuilder.createClient();
        client.property(ClientProperties.FOLLOW_REDIRECTS, false);
        annotationTarget = client.target(baseUrl + "/stormpath");
    }

    @Test
    public void Can_sign_in_to_all_security_contexts_with_jwt_cookie() throws IOException {

        final HttpMockHandler handler = http.mock(mock(HttpMockHandler.class));
        final String username = "test";
        final String password = "Password1";
        final Form form = new Form();
        final HttpMockResponse currentTenantResponse = mock(HttpMockResponse.class);
        final String tenantId = someAlphaNumericString(22);
        final HttpMockResponse tenantResponse = mock(HttpMockResponse.class);
        final HttpMockResponse applicationResponse = mock(HttpMockResponse.class);
        final String applicationId = someAlphaNumericString(22);
        final String accountId = someAlphaNumericString(22);
        final HttpMockResponse authenticationResponse = mock(HttpMockResponse.class);
        final String authenticationId = someAlphaNumericString(22);
        final HttpMockResponse groupsResponse = mock(HttpMockResponse.class);

        // Given
        form.param("username", username);
        form.param("password", password);
        given(handler.get("/tenants/current")).willReturn(currentTenantResponse);
        given(currentTenantResponse.getStatus()).willReturn(302);
        given(currentTenantResponse.getHeaders()).willReturn(headers(h("Location", someTenantLocation(tenantId))));
        given(handler.get("/tenants/" + tenantId)).willReturn(tenantResponse);
        given(tenantResponse.getStatus()).willReturn(200);
        given(tenantResponse.getBodyAsString()).willReturn(tenantBody(someTenant(), tenantId));
        given(handler.get(format("/tenants/%s/applications?name=%s", tenantId, encode(applicationName, "UTF-8"))))
            .willReturn(applicationResponse);
        given(applicationResponse.getStatus()).willReturn(200);
        given(applicationResponse.getBodyAsString())
            .willReturn(applicationBody(applicationName, tenantId, applicationId, accountId));
        given(handler.post(format("/applications/%s/loginAttempts?expand=account", applicationId), loginBody(username, password)))
            .willReturn(authenticationResponse);
        given(authenticationResponse.getStatus()).willReturn(200);
        given(authenticationResponse.getBodyAsString())
            .willReturn(authenticationBody(authenticationId, tenantId, someAlphaNumericString(22)));
        given(handler.get(format("/accounts/%s/groups", authenticationId))).willReturn(groupsResponse);
        given(groupsResponse.getStatus()).willReturn(200);
        given(groupsResponse.getBodyAsString()).willReturn(groupsBody(authenticationId));
        final Response forbidden = annotationTarget.request().get();
        final Response signIn = annotationTarget.path("signIn").request().post(form(form));

        // When
        final Response annotation = annotationTarget.request().cookie(signIn.getCookies().get("JSESSIONID")).get();

        // Then
        assertThat(forbidden.getStatus(), is(FORBIDDEN.getStatusCode()));
        assertThat(signIn.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.getStatus(), is(OK.getStatusCode()));
        assertThat(annotation.readEntity(String.class), is(TEXT));
    }

    private String someTenantLocation(String tenantId) {
        return format("%s/mock/tenants/%s", baseUrl, tenantId);
    }

    private static String someTenant() {
        return format("%s-%s", someAlphaString(5), someAlphaString(8));
    }

    private String tenantBody(String tenant, String tenantId) throws IOException {
        return stormpathBody("stormpath-tenant.json", tenant, tenantId);
    }

    private String applicationBody(String applicationName, String tenantId, String applicationId, String accountId)
        throws IOException {
        return stormpathBody("stormpath-application.json", applicationName, tenantId, applicationId,
            accountId);
    }

    private String authenticationBody(String authenticationId, String tenantId, String directoryId) throws IOException {
        return stormpathBody("stormpath-authentication.json", authenticationId, tenantId, directoryId);
    }

    private String loginBody(String username, String password) throws IOException {
        return fileBody("stormpath-login.json", printBase64Binary(
            format("%s:%s", username, password).getBytes()
        )).replaceAll("\\n", "").replaceAll("\\s", "");
    }

    private String groupsBody(String authenticationId) throws IOException {
        return stormpathBody("stormpath-groups.json", authenticationId);
    }

    private String stormpathBody(String fileName, Object... parts)
        throws IOException {
        final List<Object> partList = new ArrayList<>(asList(parts));
        partList.add(0, baseUrl + "/mock");
        return fileBody(fileName, partList.toArray(new Object[partList.size()]));
    }

    private String fileBody(String fileName, Object... parts)
        throws IOException {
        return format(
            IOUtils.toString(
                Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(fileName),
                "UTF-8"
            ),
            parts
        );
    }
}
