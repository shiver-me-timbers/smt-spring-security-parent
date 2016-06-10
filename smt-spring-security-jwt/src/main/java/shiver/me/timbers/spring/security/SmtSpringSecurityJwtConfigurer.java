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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Karl Bennett
 */
@Configuration
public class SmtSpringSecurityJwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    public static SmtSpringSecurityJwtConfigurer smtJwt() {
        return new SmtSpringSecurityJwtConfigurer();
    }

    @Value("${smt.spring.security.jwt.loginUrl:/login}")
    private String loginUrl;

    @Value("${smt.spring.security.jwt.logoutUrl:/logout}")
    private String logoutUrl;

    @Value("${smt.spring.security.jwt.logoutSuccessUrl:/}")
    private String logoutSuccessUrl;

    @Value("${smt.spring.security.jwt.usernameParameter:username}")
    private String usernameParameter;

    @Value("${smt.spring.security.jwt.passwordParameter:password}")
    private String passwordParameter;

    @Autowired
    private JwtAuthenticationSuccessHandler successHandler;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @Autowired
    private JwtLogoutHandler logoutHandler;

    @Override
    public void init(HttpSecurity http) throws Exception {
        autowireThisBean(http);

        http.formLogin()
            .loginPage(loginUrl)
            .successHandler(successHandler)
            .usernameParameter(usernameParameter)
            .passwordParameter(passwordParameter)
            .permitAll();

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.logout()
            .logoutUrl(logoutUrl)
            .logoutSuccessUrl(logoutSuccessUrl)
            .addLogoutHandler(logoutHandler)
            .permitAll();
    }

    private void autowireThisBean(final HttpSecurity http) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setParent(http.getSharedObject(ApplicationContext.class));
        context.register(PropertySourcesPlaceholderConfigurer.class);
        context.register(JwtSpringSecurityConfiguration.class);
        context.refresh();
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }
}
