<!---
Copyright 2015 Karl Bennett

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
smt-spring-security-parent
===========
[![Build Status](https://travis-ci.org/shiver-me-timbers/smt-spring-security-parent.svg)](https://travis-ci.org/shiver-me-timbers/smt-spring-security-parent) [![Coverage Status](https://coveralls.io/repos/shiver-me-timbers/smt-spring-security-parent/badge.svg?branch=master&service=github)](https://coveralls.io/github/shiver-me-timbers/smt-spring-security-parent?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.shiver-me-timbers/smt-spring-security-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.shiver-me-timbers/smt-spring-security-parent/)

This is the parent project that groups all the smt-spring-security libraries.

## Libraries

### [smt-spring-security-jwt](smt-spring-security-jwt)

This library will automatically enable stateless JWT authentication for any Spring Security configuration. It is applied
with an annotation and weaves itself into your existing Spring Security setup so will not override any of your current
configuration e.g. AuthenticationSuccessHandlers, LogoutSuccessHandlers.

#### Usage

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import shiver.me.timbers.spring.security.EnableJwtAuthentication;

@EnableWebSecurity
@EnableJwtAuthentication // Just add this annotation and configure Spring Security how ever you normally would.
public class JwtSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
        http.formLogin().loginPage("/signIn").defaultSuccessUrl("/").permitAll();
        http.logout().logoutUrl("/signOut").logoutSuccessUrl("/");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }
}
```