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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.account.VerificationEmailRequest;
import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList;
import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.application.ApplicationStatus;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteUrlBuilder;
import com.stormpath.sdk.oauth.OauthPolicy;
import com.stormpath.sdk.oauth.OauthRequestAuthenticator;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.saml.SamlCallbackHandler;
import com.stormpath.sdk.saml.SamlIdpUrlBuilder;
import com.stormpath.sdk.saml.SamlPolicy;
import com.stormpath.sdk.tenant.Tenant;
import org.springframework.core.env.Environment;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.stormpath.sdk.application.Applications.name;
import static com.stormpath.sdk.application.Applications.where;
import static java.lang.String.format;

public class DeferredApplication implements Application {

    private final String apiKeyId;
    private final String apiKeySecret;
    private final String applicationName;
    private final Environment environment;
    private Application application;

    public DeferredApplication(
        String apiKeyId,
        String apiKeySecret,
        String applicationName,
        Environment environment
    ) {
        this.apiKeyId = apiKeyId;
        this.apiKeySecret = apiKeySecret;
        this.applicationName = applicationName;
        this.environment = environment;
    }

    private Application application() {
        if (application == null) {
            application = buildApplication();
        }
        return application;
    }

    private Application buildApplication() {
        return Clients.builder()
            .setBaseUrl(format("http://localhost:%s/mock", environment.getProperty("local.server.port")))
            .setApiKey(ApiKeys.builder().setId(apiKeyId).setSecret(apiKeySecret).build()).build()
            .getCurrentTenant().getApplications(where(name().eqIgnoreCase(applicationName))).iterator().next();
    }

    @Override
    public String getName() {
        return application().getName();
    }

    @Override
    public Application setName(String name) {
        return application().setName(name);
    }

    @Override
    public String getDescription() {
        return application().getDescription();
    }

    @Override
    public Application setDescription(String description) {
        return application().setDescription(description);
    }

    @Override
    public ApplicationStatus getStatus() {
        return application().getStatus();
    }

    @Override
    public Application setStatus(ApplicationStatus status) {
        return application().setStatus(status);
    }

    @Override
    public AccountList getAccounts() {
        return application().getAccounts();
    }

    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        return application().getAccounts(queryParams);
    }

    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        return application().getAccounts(criteria);
    }

    @Override
    public Account createAccount(Account account) throws ResourceException {
        return application().createAccount(account);
    }

    @Override
    public Account createAccount(CreateAccountRequest request) throws ResourceException {
        return application().createAccount(request);
    }

    @Override
    public GroupList getGroups() {
        return application().getGroups();
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        return application().getGroups(queryParams);
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        return application().getGroups(criteria);
    }

    @Override
    public Group createGroup(Group group) throws ResourceException {
        return application().createGroup(group);
    }

    @Override
    public Group createGroup(CreateGroupRequest request) {
        return application().createGroup(request);
    }

    @Override
    public Tenant getTenant() {
        return application().getTenant();
    }

    @Override
    public PasswordResetToken sendPasswordResetEmail(String email) throws ResourceException {
        return application().sendPasswordResetEmail(email);
    }

    @Override
    public PasswordResetToken sendPasswordResetEmail(String email, AccountStore accountStore) throws ResourceException {
        return application().sendPasswordResetEmail(email, accountStore);
    }

    @Override
    public Account verifyPasswordResetToken(String token) {
        return application().verifyPasswordResetToken(token);
    }

    @Override
    public Account resetPassword(String passwordResetToken, String newPassword) {
        return application().resetPassword(passwordResetToken, newPassword);
    }

    @Override
    public AuthenticationResult authenticateAccount(AuthenticationRequest request) throws ResourceException {
        return application().authenticateAccount(request);
    }

    @Override
    public ProviderAccountResult getAccount(ProviderAccountRequest request) {
        return application().getAccount(request);
    }

    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings() {
        return application().getAccountStoreMappings();
    }

    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams) {
        return application().getAccountStoreMappings(queryParams);
    }

    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings(ApplicationAccountStoreMappingCriteria criteria) {
        return application().getAccountStoreMappings(criteria);
    }

    @Override
    @Deprecated
    public ApplicationAccountStoreMappingList getApplicationAccountStoreMappings(ApplicationAccountStoreMappingCriteria criteria) {
        return application().getApplicationAccountStoreMappings(criteria);
    }

    @Override
    public ApplicationAccountStoreMapping createAccountStoreMapping(ApplicationAccountStoreMapping mapping) throws ResourceException {
        return application().createAccountStoreMapping(mapping);
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException {
        return application().addAccountStore(accountStore);
    }

    @Override
    public ApiKey getApiKey(String id) throws ResourceException, IllegalArgumentException {
        return application().getApiKey(id);
    }

    @Override
    public ApiKey getApiKey(String id, ApiKeyOptions options) throws ResourceException, IllegalArgumentException {
        return application().getApiKey(id, options);
    }

    @Override
    @Deprecated
    public ApiAuthenticationResult authenticateApiRequest(Object httpRequest) throws IllegalArgumentException, ResourceException {
        return application().authenticateApiRequest(httpRequest);
    }

    @Override
    @Deprecated
    public OauthRequestAuthenticator authenticateOauthRequest(Object httpRequest) throws IllegalArgumentException {
        return application().authenticateOauthRequest(httpRequest);
    }

    @Override
    public IdSiteUrlBuilder newIdSiteUrlBuilder() {
        return application().newIdSiteUrlBuilder();
    }

    @Override
    public SamlIdpUrlBuilder newSamlIdpUrlBuilder() {
        return application().newSamlIdpUrlBuilder();
    }

    @Override
    public IdSiteCallbackHandler newIdSiteCallbackHandler(Object httpRequest) {
        return application().newIdSiteCallbackHandler(httpRequest);
    }

    @Override
    public SamlCallbackHandler newSamlCallbackHandler(Object httpRequest) {
        return application().newSamlCallbackHandler(httpRequest);
    }

    @Override
    public void sendVerificationEmail(VerificationEmailRequest verificationEmailRequest) {
        application().sendVerificationEmail(verificationEmailRequest);
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(String hrefOrName) {
        return application().addAccountStore(hrefOrName);
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(DirectoryCriteria criteria) {
        return application().addAccountStore(criteria);
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(OrganizationCriteria criteria) {
        return application().addAccountStore(criteria);
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(GroupCriteria criteria) {
        return application().addAccountStore(criteria);
    }

    @Override
    public Application saveWithResponseOptions(ApplicationOptions responseOptions) {
        return application().saveWithResponseOptions(responseOptions);
    }

    @Override
    public OauthPolicy getOauthPolicy() {
        return application().getOauthPolicy();
    }

    @Override
    public SamlPolicy getSamlPolicy() {
        return application().getSamlPolicy();
    }

    @Override
    public List<String> getAuthorizedCallbackUris() {
        return application().getAuthorizedCallbackUris();
    }

    @Override
    public Application setAuthorizedCallbackUris(List<String> authorizedCallbackUris) {
        return application().setAuthorizedCallbackUris(authorizedCallbackUris);
    }

    @Override
    public Application addAuthorizedCallbackUri(String authorizedCallbackUri) {
        return application().addAuthorizedCallbackUri(authorizedCallbackUri);
    }

    @Override
    public AccountStore getDefaultAccountStore() {
        return application().getDefaultAccountStore();
    }

    @Override
    public void setDefaultAccountStore(AccountStore accountStore) {
        application().setDefaultAccountStore(accountStore);
    }

    @Override
    public AccountStore getDefaultGroupStore() {
        return application().getDefaultGroupStore();
    }

    @Override
    public void setDefaultGroupStore(AccountStore accountStore) {
        application().setDefaultGroupStore(accountStore);
    }

    @Override
    public String getHref() {
        return application().getHref();
    }

    @Override
    public void save() {
        application().save();
    }

    @Override
    public void delete() {
        application().delete();
    }

    @Override
    public CustomData getCustomData() {
        return application().getCustomData();
    }

    @Override
    public Date getCreatedAt() {
        return application().getCreatedAt();
    }

    @Override
    public Date getModifiedAt() {
        return application().getModifiedAt();
    }
}
