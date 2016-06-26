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

This library will automatically enable stateless, role supporting, JWT authentication for any Spring Security
configuration. It can be applied with either an
[annotation](src/main/java/shiver/me/timbers/spring/security/EnableJwtAuthentication.java) or an
[adaptor](src/main/java/shiver/me/timbers/spring/security/JwtSpringSecurityAdaptor.java) and weaves itself into your
existing Spring Security setup so will not override any of your current configuration
e.g. AuthenticationSuccessHandlers, LogoutSuccessHandlers.

#### Annotation

The `@EnableJwtAuthentication` annotation should be used if you wish to enable JWT authentication for all of your Spring
Security configurations that you might have setup for different paths. Just add the annotation to any configuration
class to enable JWT authentication across your entire application.

#### Adaptor

The `JwtSpringSecurityAdaptor` should be used if you only want JWT authentication applied to specific Spring security
configurations. The adaptor configuration will fail if the annotation is present, this is because it is redundant when
the annotation has already been applied.

## Usage

#### Annotation

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import shiver.me.timbers.spring.security.EnableJwtAuthentication;

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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static shiver.me.timbers.spring.security.JwtSpringSecurityAdaptor.jwt;

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

## Configuration

The JWT authentication can be configured with properties, these are defined in the
[`JwtConfiguration`](src/main/java/shiver/me/timbers/spring/security/JwtConfiguration.java) class.

#### Compulsory Properties

A secret value must be provided for generating the JWT tokens. This can be done by setting either one of the following
two properties.
```properties
smt.spring.security.jwt.secret=some secret
smt.spring.security.jwt.secretFile=the/path/to/some/secret/file
```

##### smt.spring.security.jwt.secret
This property can be set with the secret value of your choosing.

##### smt.spring.security.jwt.secretFile
This property must contain the path to a file that only contains the value that you would have otherwise set with the
`smt.spring.security.jwt.secret` property.

##### The secret value
Different secret values must be used depending on the `smt.spring.security.jwt.algorithm`
(see Optional Properties below) that is chosen.

HMAC algorithms (`HS256`, `HS384`, `HS512`): The value can be
[any string you like](../smt-spring-security-test/smt-spring-security-jwt-hmac-integration/src/test/resources/application-value.properties).

RSA algorithms (`RS256`, `RS384`, `RS512`, `PS256`, `PS384`, `PS512`): The value must be a valid
[Base64 RSA private key](../smt-spring-security-test/smt-spring-security-jwt-rsa-integration/src/test/resources/application-value.properties)
in the standard file format that includes the `BEGIN` and `END` strings and line breaks. The easiest way to produce one
of these keys is with the following command.
```bash
ssh-keygen -t rsa
```

ECDSA algorithms (`ES256`, `ES384`, `ES512`): The value must be a valid
[Base64 ECDSA private key](../smt-spring-security-test/smt-spring-security-jwt-ecdsa-integration/src/test/resources/application-value.properties)
with the same requirements as the RSA key. The command for generating one of these keys is also similar.
```bash
ssh-keygen -t ecdsa
```



#### Optional Properties

Further configuration can be achieved with the following properties:
```properties
# The name of the JWT token, this will set the name of the head and cookie that will be returned in the
# response of a successful login.
# DEFAULT: X-AUTH-TOKEN
smt.spring.security.jwt.tokenName=some_token_name
# The hashing algorithm used when generating the JWT token.
# See: io.jsonwebtoken.SignatureAlgorithm
# VALUES: NONE, HS256, HS384, HS512, RS256, RS384, RS512, ES256, ES384, ES512, PS256, PS384, PS512
# DEFAULT: HS512
smt.spring.security.jwt.algorithm=RS512
# The duration that the token will be valid for. It is in relation to the expiryUnit below. This will also
# be the Max-Age of the JWT token cookie.
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
context. So for example, if you wished to use your own custom principal class you could supply your own `JwtTokenParser`
and `AuthenticationConverter` beans.

```java
@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
@EnableJwtAuthentication
public class JwtCustomPrincipalSecurityConfigurationAnnotation extends WebSecurityConfigurerAdapter {

    @Value("${smt.spring.security.jwt.algorithm:HS512}")
    private SignatureAlgorithm algorithm;

    @Value("${smt.spring.security.jwt.token.expiryDuration:-1}")
    private int expiryDuration;

    @Value("${smt.spring.security.jwt.token.expiryUnit:MINUTES}")
    private TimeUnit expiryUnit;

    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/custom/**");
        http.csrf().disable();
        http.authorizeRequests().anyRequest().authenticated();
        http.formLogin().loginPage("/custom/signIn").defaultSuccessUrl("/").permitAll();
        http.logout().logoutUrl("/custom/signOut").logoutSuccessUrl("/");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }

    @Bean
    public AuthenticationConverter<CustomPrincipal> authenticationConverter() {
        return new CustomPrincipleAuthenticationConverter();
    }

    @Bean
    @Autowired
    public JwtTokenParser<CustomPrincipal, String> jwtTokenParser(
        JwtBuilder builder,
        JwtParser parser,
        KeyPair keyPair,
        Clock clock,
        ObjectMapper objectMapper
    ) {
        return new JJwtTokenParser<>(
            CustomPrincipal.class,
            builder,
            parser,
            algorithm,
            keyPair,
            expiryDuration,
            expiryUnit,
            clock,
            objectMapper
        );
    }
}
```