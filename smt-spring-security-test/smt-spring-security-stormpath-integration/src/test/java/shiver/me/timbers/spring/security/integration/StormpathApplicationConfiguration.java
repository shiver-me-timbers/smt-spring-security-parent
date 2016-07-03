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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile("default")
public class StormpathApplicationConfiguration {

    @Value("${smt.spring.security.stormpath.apiKey.id}")
    private String apiKeyId;

    @Value("${smt.spring.security.stormpath.apiKey.secret}")
    private String apiKeySecret;

    @Value("${smt.spring.security.stormpath.applicationName}")
    private String applicationName;

    @Bean
    public Application application(final Environment environment) {
        return new DeferredApplication(apiKeyId, apiKeySecret, applicationName, environment);
    }
}
