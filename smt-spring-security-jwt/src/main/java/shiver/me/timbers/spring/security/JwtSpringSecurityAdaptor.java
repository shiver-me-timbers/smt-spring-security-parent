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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shiver.me.timbers.spring.security.fields.FieldMutator;
import shiver.me.timbers.spring.security.fields.Updater;

/**
 * @author Karl Bennett
 */
@Configuration
@Import({JwtConfiguration.class, JwtModificationConfiguration.class})
public class JwtSpringSecurityAdaptor extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    public static JwtSpringSecurityAdaptor jwt() {
        return new JwtSpringSecurityAdaptor();
    }

    @Autowired
    private JwtLogoutHandler logoutHandler;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @Autowired
    private JwtAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private FieldMutator fieldMutator;

    @Override
    public void init(HttpSecurity http) throws Exception {
        autowireThis(http);

        http.logout().addLogoutHandler(logoutHandler);
        fieldMutator.update(
            http.formLogin(),
            "authFilter",
            AbstractAuthenticationProcessingFilter.class,
            new UsernamePasswordAuthenticationFilterWrapper()
        );
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private void autowireThis(HttpSecurity http) {
        final ApplicationContext parent = http.getSharedObject(ApplicationContext.class);
        checkForJwtAnnotation(parent);

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setParent(parent);
        context.register(PropertySourcesPlaceholderConfigurer.class);
        context.register(getClass());
        context.refresh();
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }

    private static void checkForJwtAnnotation(ApplicationContext parent) {
        for (String name : parent.getBeanDefinitionNames()) {
            if (name.contains(JwtSpringSecurityConfiguration.class.getName())) {
                throw new IllegalStateException(
                    "The @EnableJwtAuthentication has already been applied so the JWT adaptor configuration is redundant."
                );
            }
        }
    }

    private class UsernamePasswordAuthenticationFilterWrapper implements Updater<AbstractAuthenticationProcessingFilter> {
        @Override
        public AbstractAuthenticationProcessingFilter update(AbstractAuthenticationProcessingFilter filter) {
            return new WrappedUsernamePasswordAuthenticationFilter(
                fieldMutator, (UsernamePasswordAuthenticationFilter) filter, authenticationSuccessHandler
            );
        }
    }
}
