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

import com.stormpath.sdk.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author Karl Bennett
 */
@EnableWebSecurity
@Import(StormpathConfiguration.class)
public class StormpathSpringSecurityConfiguration {

    @Autowired
    public void configureGlobal(
        AuthenticationManagerBuilder auth,
        Application application,
        StormpathAuthenticationRequestFactory requests,
        UserDetailsConverter converter
    ) throws Exception {
        auth.authenticationProvider(new StormpathAuthenticationProvider(application, requests, converter));
    }
}
