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
smt-spring-security-jwt
===========
[![Build Status](https://travis-ci.org/shiver-me-timbers/smt-spring-security-parent.svg)](https://travis-ci.org/shiver-me-timbers/smt-spring-security-parent) [![Coverage Status](https://coveralls.io/repos/shiver-me-timbers/smt-spring-security-parent/badge.svg?branch=master&service=github)](https://coveralls.io/github/shiver-me-timbers/smt-spring-security-parent?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.shiver-me-timbers/smt-spring-security-jwt/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.shiver-me-timbers/smt-spring-security-jwt/)

This library will automatically enable stateless JWT authentication for any Spring Security configuration. It is applied
with an annotation and weaves itself into your existing configuration so will not override any of your current
configuration e.g. AuthenticationSuccessHandlers, LogoutSuccessHandlers.

## Usage

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

## Configuration

The JWT authentication can be configured with properties, these are defined in two configuration classes:
[`JwtConfiguration`](src/main/java/shiver/me/timbers/spring/security/JwtConfiguration.java) and
[`SmtSpringSecurityJwtConfiguration`](src/main/java/shiver/me/timbers/spring/security/SmtSpringSecurityJwtConfiguration.java)

#### Compulsory Properties

The only property that must be set for this library to run is the JWT secret.
```properties
smt.spring.security.jwt.secret=some secret
```

This property must be set with different values depending on the `smt.spring.security.jwt.algorithm`
(see properties below) that is chosen.

HMAC algorithms (`HS256`, `HS384`, `HS512`): The value can be
[any string you like](src/test/resources/application-test.properties).

RSA algorithms (`RS256`, `RS384`, `RS512`, `PS256`, `PS384`, `PS512`): The value must be a valid
[Base64 RSA private key](src/test/resources/application-test.properties) in the standard file format that includes the
`BEGIN` and `END` strings and line breaks. The easiest way to produce one of these keys is with the following command.
```bash
ssh-keygen -t rsa
```

ECDSA algorithms (`ES256`, `ES384`, `ES512`): The value must be a valid
[Base64 ECDSA private key](src/test/resources/application-test.properties) with the same requirements as the RSA key.
The command for generating one of these keys is also similar.
```bash
ssh-keygen -t ecdsa
```



#### Optional Properties

Further configuration can be achieved with the following properties:
```properties
# The name of the JWT token, this will set the name of the head and cookie that will be returned in the response of a
# successful login.
# DEFAULT: X-AUTH-TOKEN
smt.spring.security.jwt.tokenName=some_token_name
# The hashing algorithm used when generating the JWT token.
# See: io.jsonwebtoken.SignatureAlgorithm
# VALUES: NONE, HS256, HS384, HS512, RS256, RS384, RS512, ES256, ES384, ES512, PS256, PS384, PS512
# DEFAULT: HS512
smt.spring.security.jwt.algorithm=RS512
# The duration that the token will be valid for. It is in relation to the expiryUnit below. This will also be the
# Max-Age of the JWT token cookie.
# DEFAULT: -1 (does not expire/session cookie)
smt.spring.security.jwt.token.expiryDuration=30
# The unit of time for the expiryDuration above. If the expiry is -1 this property is ignored.
# See: java.util.concurrent.TimeUnit
# VALUES: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
# DEFAULT: MINUTES
smt.spring.security.jwt.token.expiryUnit=SECONDS
# Domain of the JWT cookie.
# DEFAULT: (empty)
smt.spring.security.jwt.cookie.domain=shiver.me.timbers
# Path of the JWT cookie.
# DEFAULT: /
smt.spring.security.jwt.cookie.path=/shiver/me/timbers
# "Secure" flag of the JWT cookie.
# DEFAULT: false
smt.spring.security.jwt.cookie.secure=true
# "HttpOnly" flag of the JWT cookie.
# DEFAULT: false
smt.spring.security.jwt.cookie.httpOnly=true
```

#### Advanced Configuration

Every class that is used to compose this library can be overridden by adding your own implementation to the Spring
context.

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
public class CustomJwtConfiguration {

    @Bean
    @Autowired
    public JwtAuthenticationFilter jwtAuthenticationFilter(
        JwtTokenParser<Authentication, HttpServletRequest> tokenParser,
        SecurityContextHolder securityContextHolder
    ) {
        return new CookieAndHeaderJwtAuthenticationFilter(tokenParser, securityContextHolder) {

            final Logger log = LoggerFactory.getLogger(CookieAndHeaderJwtAuthenticationFilter.class);

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                log.info("JWT authentication attempt for: " + ((HttpServletRequest) request).getPathInfo());
                super.doFilter(request, response, chain);
            }
        };
    }
}
```