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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import shiver.me.timbers.spring.security.JwtAuthentication;
import shiver.me.timbers.spring.security.jwt.AuthenticationConverter;

public class CustomPrincipleAuthenticationConverter implements AuthenticationConverter<CustomPrincipal> {

    @Override
    public CustomPrincipal convert(Authentication authentication) {
        return new CustomPrincipal(extractUsername(authentication));
    }

    private static String extractUsername(Authentication authentication) {
        final Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }

    @Override
    public Authentication convert(CustomPrincipal principal) {
        return new JwtAuthentication(principal.getUsername(), null);
    }
}
