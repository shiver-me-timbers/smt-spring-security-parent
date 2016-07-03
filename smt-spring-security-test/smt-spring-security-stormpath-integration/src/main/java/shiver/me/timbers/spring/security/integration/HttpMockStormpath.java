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
import shiver.me.timbers.http.mock.HttpMockHandler;
import shiver.me.timbers.http.mock.HttpMockResponse;
import shiver.me.timbers.http.mock.HttpMockServer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.util.Arrays.asList;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.http.mock.HttpMock.h;
import static shiver.me.timbers.http.mock.HttpMock.headers;

public class HttpMockStormpath implements MockStormpath {

    private final String applicationName;
    private final HttpMockHandler handler;
    private final String tenantId;
    private final String applicationId;
    private final String authenticationId;
    private final String directoryId;

    public HttpMockStormpath(String applicationName, HttpMockServer http) {
        this.applicationName = applicationName;
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
        handler = http.mock(mock(HttpMockHandler.class));
        tenantId = someAlphaNumericString(22);
        applicationId = someAlphaNumericString(22);
        authenticationId = someAlphaNumericString(22);
        directoryId = someAlphaNumericString(22);
    }

    @Override
    public void mockCurrentTenant(String baseUrl) throws IOException {
        final HttpMockResponse currentTenantResponse = mock(HttpMockResponse.class);
        given(handler.get(currentTenantsPath())).willReturn(currentTenantResponse);
        given(currentTenantResponse.getStatus()).willReturn(302);
        given(currentTenantResponse.getHeaders())
            .willReturn(headers(h("Location", someTenantLocation(baseUrl, tenantId))));
    }

    @Override
    public void mockTenant(String baseUrl) throws IOException {
        final HttpMockResponse tenantResponse = mock(HttpMockResponse.class);
        given(handler.get(tenantsPath())).willReturn(tenantResponse);
        given(tenantResponse.getStatus()).willReturn(200);
        given(tenantResponse.getBodyAsString()).willReturn(tenantBody(baseUrl, someTenant(), tenantId));
    }

    @Override
    public void mockApplication(String baseUrl) throws IOException {
        final HttpMockResponse applicationResponse = mock(HttpMockResponse.class);
        given(handler.get(applicationPath())).willReturn(applicationResponse);
        given(applicationResponse.getStatus()).willReturn(200);
        given(applicationResponse.getBodyAsString())
            .willReturn(applicationBody(baseUrl, applicationName, tenantId, applicationId, someAlphaNumericString(22)));
    }

    @Override
    public void mockLogin(String baseUrl, String username, String password) throws IOException {
        mockLogin(baseUrl, username, password, authenticationId);
    }

    @Override
    public void mockLogin(String baseUrl, String username, String password, String authenticationId)
        throws IOException {
        final HttpMockResponse authenticationResponse = mock(HttpMockResponse.class);
        given(handler.post(loginPath(), loginBody(username, password))).willReturn(authenticationResponse);
        given(authenticationResponse.getStatus()).willReturn(200);
        given(authenticationResponse.getBodyAsString())
            .willReturn(authenticationBody(baseUrl, authenticationId, tenantId, directoryId));
    }

    @Override
    public void mockEmptyGroups(String baseUrl) throws IOException {
        mockGroups("", baseUrl, authenticationId);
    }

    @Override
    public void mockGroups(String roleName, String baseUrl, String authenticationId) throws IOException {
        final HttpMockResponse groupsResponse = mock(HttpMockResponse.class);
        final String fileName = format("stormpath-groups%s.json", roleName.isEmpty() ? "" : "-" + roleName);
        given(handler.get(groupsPath(authenticationId))).willReturn(groupsResponse);
        given(groupsResponse.getStatus()).willReturn(200);
        given(groupsResponse.getBodyAsString())
            .willReturn(
                groupsBody(fileName, baseUrl, authenticationId, someAlphaNumericString(22), directoryId, tenantId)
            );
    }

    @Override
    public void verifyCurrentTenant() throws IOException {
        verify(handler).get(currentTenantsPath());
    }

    @Override
    public void verifyTenant() throws IOException {
        verify(handler).get(tenantsPath());
    }

    @Override
    public void verifyApplication() throws IOException {
        verify(handler).get(applicationPath());
    }

    @Override
    public void verifyLogin(String username, String password) throws IOException {
        verify(handler).post(loginPath(), loginBody(username, password));
    }

    @Override
    public void verifyEmptyGroups() throws IOException {
        verify(handler).get(groupsPath(authenticationId));
    }

    @Override
    public void verifyGroups(String authenticationId) throws IOException {
        verify(handler).get(groupsPath(authenticationId));
    }

    private String currentTenantsPath() {
        return "/tenants/current";
    }

    private String tenantsPath() {
        return "/tenants/" + tenantId;
    }

    private String applicationPath() throws UnsupportedEncodingException {
        return format(
            "/tenants/%s/applications?name=%s",
            tenantId, encode(applicationName, "UTF-8")
        );
    }

    private String loginPath() {
        return format("/applications/%s/loginAttempts?expand=account", applicationId);
    }

    private String groupsPath(String authenticationId) {
        return format("/accounts/%s/groups", authenticationId);
    }

    private static String someTenantLocation(String baseUrl, String tenantId) {
        return format("%s/mock/tenants/%s", baseUrl, tenantId);
    }

    private static String someTenant() {
        return format("%s-%s", someAlphaString(5), someAlphaString(8));
    }

    private static String tenantBody(String baseUrl, String tenant, String tenantId) throws IOException {
        return stormpathBody("stormpath-tenant.json", baseUrl, tenant, tenantId);
    }

    private static String applicationBody(
        String baseUrl,
        String applicationName,
        String tenantId,
        String applicationId,
        String accountId
    )
        throws IOException {
        return stormpathBody("stormpath-application.json", baseUrl, applicationName, tenantId, applicationId,
            accountId);
    }

    private static String authenticationBody(String baseUrl, String authenticationId, String tenantId, String directoryId)
        throws IOException {
        return stormpathBody("stormpath-authentication.json", baseUrl, authenticationId, tenantId, directoryId);
    }

    private static String loginBody(String username, String password) throws IOException {
        return fileBody("stormpath-login.json", printBase64Binary(
            format("%s:%s", username, password).getBytes()
        )).replaceAll("\\n", "").replaceAll("\\s", "");
    }

    private static String groupsBody(
        String fileName,
        String baseUrl,
        String authenticationId,
        String groupId,
        String directoryId,
        String tenantId
    ) throws IOException {
        return stormpathBody(fileName, baseUrl, authenticationId, groupId, directoryId, tenantId);
    }

    private static String stormpathBody(String fileName, String baseUrl, Object... parts)
        throws IOException {
        final List<Object> partList = new ArrayList<>(asList(parts));
        partList.add(0, baseUrl + "/mock");
        return fileBody(fileName, partList.toArray(new Object[partList.size()]));
    }

    private static String fileBody(String fileName, Object... parts)
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
