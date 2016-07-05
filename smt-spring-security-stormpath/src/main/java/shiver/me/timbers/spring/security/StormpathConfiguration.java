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

import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.stormpath.sdk.application.Applications.name;
import static com.stormpath.sdk.application.Applications.where;

/**
 * @author Karl Bennett
 */
@Configuration
public class StormpathConfiguration {

    @Value("${smt.spring.security.stormpath.client.apiKey.id:}")
    private String apiKeyId;

    @Value("${smt.spring.security.stormpath.client.apiKey.secret:}")
    private String apiKeySecret;

    @Value("${smt.spring.security.stormpath.application.name:}")
    private String applicationName;

    @Value("${smt.spring.security.stormpath.application.href:}")
    private String applicationHref;

    @Bean
    @ConditionalOnMissingBean(Application.class)
    public Application application(Client client) {
        if (applicationName.isEmpty() && applicationHref.isEmpty()) {
            throw new IllegalStateException(
                "Either one of (smt.spring.security.stormpath.application.name) or (smt.spring.security.stormpath.application.href) must be set."
            );
        }

        if (!applicationHref.isEmpty()) {
            return client.getResource(applicationHref, Application.class);
        }

        return client.getCurrentTenant().getApplications(where(name().eqIgnoreCase(applicationName))).iterator().next();
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client client() {
        return Clients.builder().setApiKey(ApiKeys.builder().setId(apiKeyId).setSecret(apiKeySecret).build()).build();
    }

    @Bean
    @ConditionalOnMissingBean(StormpathAuthenticationRequestFactory.class)
    public StormpathAuthenticationRequestFactory stormpathAuthenticationRequestFactory(
        StormpathRequestBuilderFactory builderFactory
    ) {
        return new UsernamePasswordStormpathAuthenticationRequestFactory(builderFactory);
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsConverter.class)
    public UserDetailsConverter userDetailsConverter(GroupGrantedAuthorityConverter authorityConverter) {
        return new StormpathUserDetailsConverter(authorityConverter);
    }

    @Bean
    @ConditionalOnMissingBean(StormpathRequestBuilderFactory.class)
    public StormpathRequestBuilderFactory stormpathRequestBuilderFactory() {
        return new UsernamePasswordStormpathRequestBuilderFactory();
    }

    @Bean
    @ConditionalOnMissingBean(GroupGrantedAuthorityConverter.class)
    public GroupGrantedAuthorityConverter groupGrantedAuthorityConverter() {
        return new StormpathGroupGrantedAuthorityConverter();
    }
}
