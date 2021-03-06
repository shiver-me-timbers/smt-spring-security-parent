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

import com.stormpath.sdk.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import shiver.me.timbers.http.mock.HttpMockServer;
import shiver.me.timbers.spring.security.ApplicationFactory;

@Configuration
@Profile("default")
public class StormpathMockApplicationConfiguration {

    @Value("${smt.spring.security.stormpath.client.apiKey.id}")
    private String apiKeyId;

    @Value("${smt.spring.security.stormpath.client.apiKey.secret}")
    private String apiKeySecret;

    @Value("${smt.spring.security.stormpath.application.name:}")
    private String applicationName;

    @Value("${smt.spring.security.stormpath.application.href:}")
    private String applicationHref;

    @Bean
    public Application application(ApplicationFactory applicationFactory, final Environment environment) {
        return new DeferredApplication(
            applicationFactory,
            apiKeyId,
            apiKeySecret,
            applicationHref,
            applicationName,
            environment
        );
    }

    @Bean
    @Autowired
    public MockStormpath stormpathMock(HttpMockServer http) {
        return new HttpMockStormpath(applicationName, http);
    }
}
