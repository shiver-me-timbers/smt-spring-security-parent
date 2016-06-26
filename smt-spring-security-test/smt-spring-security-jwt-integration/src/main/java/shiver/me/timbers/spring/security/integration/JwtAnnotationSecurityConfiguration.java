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

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import shiver.me.timbers.spring.security.EnableJwtAuthentication;

@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
@EnableJwtAuthentication
@Import({SpringSecurityConfiguration.class, SpringSecurityControllerConfiguration.class})
public class JwtAnnotationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/jwt/**");
        http.csrf().disable();
        http.authorizeRequests()
            .antMatchers("/jwt/one").access("hasRole('ONE')")
            .antMatchers("/jwt/two").access("hasRole('TWO')")
            .anyRequest().authenticated();
        http.formLogin().successHandler(new NoRedirectAuthenticationSuccessHandler()).loginPage("/jwt/signIn")
            .permitAll();
        http.logout().logoutUrl("/jwt/signOut")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
        http.exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
        auth.inMemoryAuthentication().withUser("role1").password("password").roles("ONE");
        auth.inMemoryAuthentication().withUser("role2").password("password").roles("TWO");
    }
}
