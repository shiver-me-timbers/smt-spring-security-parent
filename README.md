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

This library will automatically enable stateless JWT authentication for any Spring Security configuration.

#### Usage

#### Annotation

```java
@EnableWebSecurity
// Just add this annotation and configure Spring Security how ever you normally would.
@EnableJwtAuthentication
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

#### Adaptor

```java
@EnableWebSecurity
public class JwtApplySecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        // Just apply this adaptor and configure Spring Security how ever you normally would.
        http.apply(jwt());
        http.formLogin().loginPage("/signIn").defaultSuccessUrl("/").permitAll();
        http.logout().logoutUrl("/jwt/signOut").logoutSuccessUrl("/");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }
}
```