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

import java.io.IOException;

/**
 * @author Karl Bennett
 */
public interface MockStormpath {

    void mockCurrentTenant(String baseUrl) throws IOException;

    void mockTenant(String baseUrl) throws IOException;

    void mockApplication(String baseUrl) throws IOException;

    void mockApplication(String baseUrl, String applicationId) throws IOException;

    void mockLogin(String baseUrl, String username, String password) throws IOException;

    void mockLogin(String baseUrl, String username, String password, String authenticationId) throws IOException;

    void mockEmptyGroups(String baseUrl) throws IOException;

    void mockGroups(String fileName, String baseUrl, String authenticationId) throws IOException;

    void verifyCurrentTenant() throws IOException;

    void verifyTenant() throws IOException;

    void verifyApplication() throws IOException;

    void verifyApplication(String applicationId);

    void verifyLogin(String username, String password) throws IOException;

    void verifyEmptyGroups() throws IOException;

    void verifyGroups(String authenticationId) throws IOException;
}
