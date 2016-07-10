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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("real-stormpath")
public class StormpathRealApplicationConfiguration {

    @Bean
    public MockStormpath nullStormpathMock() {
        return new MockStormpath() {
            @Override
            public void mockCurrentTenant(String baseUrl) throws IOException {
            }

            @Override
            public void mockTenant(String baseUrl) throws IOException {
            }

            @Override
            public void mockApplication(String baseUrl) throws IOException {
            }

            @Override
            public void mockApplication(String baseUrl, String applicationId) throws IOException {
            }

            @Override
            public void mockLogin(String baseUrl, String username, String password) throws IOException {
            }

            @Override
            public void mockLogin(String baseUrl, String username, String password, String authenticationId)
                throws IOException {
            }

            @Override
            public void mockEmptyGroups(String baseUrl) throws IOException {
            }

            @Override
            public void mockGroups(String fileName, String baseUrl, String authenticationId) throws IOException {
            }

            @Override
            public void verifyCurrentTenant() throws IOException {
            }

            @Override
            public void verifyTenant() throws IOException {
            }

            @Override
            public void verifyApplication() throws IOException {
            }

            @Override
            public void verifyApplication(String applicationId) {
            }

            @Override
            public void verifyLogin(String username, String password) throws IOException {
            }

            @Override
            public void verifyEmptyGroups() throws IOException {
            }

            @Override
            public void verifyGroups(String authenticationId) throws IOException {
            }
        };
    }
}
