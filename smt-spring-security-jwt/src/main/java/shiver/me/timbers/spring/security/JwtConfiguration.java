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

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import shiver.me.timbers.spring.security.context.SecurityContextHolder;
import shiver.me.timbers.spring.security.context.StaticSecurityContextHolder;
import shiver.me.timbers.spring.security.cookies.Bakery;
import shiver.me.timbers.spring.security.cookies.CookieBakery;
import shiver.me.timbers.spring.security.fields.FieldFinder;
import shiver.me.timbers.spring.security.fields.FieldGetSetter;
import shiver.me.timbers.spring.security.fields.FieldMutator;
import shiver.me.timbers.spring.security.fields.ReflectionFieldFinder;
import shiver.me.timbers.spring.security.fields.ReflectionFieldGetSetter;
import shiver.me.timbers.spring.security.fields.ReflectionFieldMutator;
import shiver.me.timbers.spring.security.io.FileReader;
import shiver.me.timbers.spring.security.io.ResourceFileReader;
import shiver.me.timbers.spring.security.jwt.AuthenticationRequestJwtTokenParser;
import shiver.me.timbers.spring.security.jwt.JwtTokenParser;
import shiver.me.timbers.spring.security.jwt.PrincipleJwtTokenParser;
import shiver.me.timbers.spring.security.keys.Base64;
import shiver.me.timbers.spring.security.keys.Base64KeyPairs;
import shiver.me.timbers.spring.security.keys.BouncyCastlePemKeyPairs;
import shiver.me.timbers.spring.security.keys.DatatypeConverterBase64;
import shiver.me.timbers.spring.security.keys.KeyParser;
import shiver.me.timbers.spring.security.keys.PemKeyPairs;
import shiver.me.timbers.spring.security.keys.SecretBase64KeyPairs;
import shiver.me.timbers.spring.security.keys.SignatureAlgorithmKeyParser;
import shiver.me.timbers.spring.security.modification.ChainModifier;
import shiver.me.timbers.spring.security.modification.JwtLogoutHandlerAdder;
import shiver.me.timbers.spring.security.modification.JwtSuccessHandlerWrapper;
import shiver.me.timbers.spring.security.modification.LogoutHandlerAdder;
import shiver.me.timbers.spring.security.modification.SecurityFilterChainModifier;
import shiver.me.timbers.spring.security.modification.SuccessHandlerWrapper;
import shiver.me.timbers.spring.security.secret.ChoosingSecretKeeper;
import shiver.me.timbers.spring.security.secret.SecretKeeper;
import shiver.me.timbers.spring.security.time.Clock;
import shiver.me.timbers.spring.security.time.DateClock;
import shiver.me.timbers.spring.security.weaving.ChainWeaver;
import shiver.me.timbers.spring.security.weaving.FilterChainProxyWeaver;
import shiver.me.timbers.spring.security.weaving.SecurityFilterChainWeaver;
import shiver.me.timbers.spring.security.weaving.Weaver;

import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.KeyPair;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(JwtConfiguration.class)
public class JwtConfiguration {

    @Value("${smt.spring.security.jwt.tokenName:X-AUTH-TOKEN}")
    private String tokenName;

    @Value("${smt.spring.security.jwt.algorithm:HS512}")
    private SignatureAlgorithm algorithm;

    @Value("${smt.spring.security.jwt.token.expiryDuration:-1}")
    private int expiryDuration;

    @Value("${smt.spring.security.jwt.token.expiryUnit:MINUTES}")
    private TimeUnit expiryUnit;

    @Value("${smt.spring.security.jwt.cookie.domain:}")
    private String domain;

    @Value("${smt.spring.security.jwt.cookie.path:/}")
    private String path;

    @Value("${smt.spring.security.jwt.cookie.secure:false}")
    private boolean secure;

    @Value("${smt.spring.security.jwt.cookie.httpOnly:false}")
    private boolean httpOnly;

    @Value("${smt.spring.security.jwt.secret:}")
    private String secret;

    @Value("${smt.spring.security.jwt.secretFile:}")
    private String secretFile;

    @Bean
    @ConditionalOnMissingBean(Weaver.class)
    @Autowired
    public Weaver weaver(FilterChainProxy filterChainProxy, ChainWeaver<SecurityFilterChain> chainWeaver) {
        return new FilterChainProxyWeaver(filterChainProxy, chainWeaver);
    }

    @Bean
    @ConditionalOnMissingBean(ChainWeaver.class)
    @Autowired
    public ChainWeaver<SecurityFilterChain> securityFilterChainWeaver(
        LogoutHandlerAdder logoutHandlerAdder,
        SuccessHandlerWrapper successHandlerWrapper,
        ChainModifier<SecurityFilterChain, Filter> modifier,
        JwtAuthenticationFilter authenticationFilter
    ) {
        return new SecurityFilterChainWeaver(logoutHandlerAdder, successHandlerWrapper, modifier, authenticationFilter);
    }

    @Bean
    @ConditionalOnMissingBean(LogoutHandlerAdder.class)
    @Autowired
    public LogoutHandlerAdder logoutHandlerAdder(FieldMutator mutator, JwtLogoutHandler logoutHandler) {
        return new JwtLogoutHandlerAdder(mutator, logoutHandler);
    }

    @Bean
    @ConditionalOnMissingBean(SuccessHandlerWrapper.class)
    @Autowired
    public SuccessHandlerWrapper successHandlerWrapper(
        FieldMutator mutator,
        JwtAuthenticationSuccessHandler successHandler
    ) {
        return new JwtSuccessHandlerWrapper(mutator, successHandler);
    }

    @Bean
    @ConditionalOnMissingBean(ChainModifier.class)
    public ChainModifier<SecurityFilterChain, Filter> modifier() {
        return new SecurityFilterChainModifier();
    }

    @Bean
    @ConditionalOnMissingBean(FieldMutator.class)
    @Autowired
    public FieldMutator fieldExtractor(FieldFinder fieldFinder, FieldGetSetter fieldGetSetter) {
        return new ReflectionFieldMutator(fieldFinder, fieldGetSetter);
    }

    @Bean
    @ConditionalOnMissingBean(FieldFinder.class)
    public FieldFinder fieldFinder() {
        return new ReflectionFieldFinder();
    }

    @Bean
    @ConditionalOnMissingBean(FieldGetSetter.class)
    public FieldGetSetter fieldGetSetter() {
        return new ReflectionFieldGetSetter();
    }

    @Bean
    @ConditionalOnMissingBean(JwtLogoutHandler.class)
    public JwtLogoutHandler jwtLogoutHandler() {
        return new CookieJwtLogoutHandler(tokenName);
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationFilter.class)
    @Autowired
    public JwtAuthenticationFilter jwtAuthenticationFilter(
        JwtTokenParser<Authentication, HttpServletRequest> authenticationRequestJwtTokenParser,
        SecurityContextHolder securityContextHolder
    ) {
        return new CookieAndHeaderJwtAuthenticationFilter(authenticationRequestJwtTokenParser, securityContextHolder);
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationSuccessHandler.class)
    @Autowired
    public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler(
        JwtTokenParser<Authentication, HttpServletRequest> authenticationRequestJwtTokenParser,
        Bakery<Cookie> bakery
    ) {
        return new CookieAndHeaderJwtAuthenticationSuccessHandler(
            tokenName,
            authenticationRequestJwtTokenParser,
            bakery
        );
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationRequestJwtTokenParser.class)
    @Autowired
    public JwtTokenParser<Authentication, HttpServletRequest> authenticationRequestJwtTokenParser(
        JwtTokenParser<String, String> principleJwtTokenParser
    ) {
        return new AuthenticationRequestJwtTokenParser(tokenName, principleJwtTokenParser);
    }

    @Bean
    @ConditionalOnMissingBean(Bakery.class)
    public Bakery<Cookie> bakery() {
        return new CookieBakery(expiryDuration, expiryUnit, domain, path, secure, httpOnly);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityContextHolder.class)
    public SecurityContextHolder securityContextHolder() {
        return new StaticSecurityContextHolder();
    }

    @Bean
    @ConditionalOnMissingBean(PrincipleJwtTokenParser.class)
    @Autowired
    public JwtTokenParser<String, String> principleJwtTokenParser(
        SecretKeeper secretKeeper,
        JwtBuilder builder,
        JwtParser parser,
        KeyPair keyPair,
        Clock clock
    ) {
        return new PrincipleJwtTokenParser(
            secretKeeper.getSecret(), builder, parser, algorithm, keyPair, expiryDuration, expiryUnit, clock
        );
    }

    @Bean
    @ConditionalOnMissingBean(SecretKeeper.class)
    public SecretKeeper secretKeeper(FileReader fileReader) {
        return new ChoosingSecretKeeper(secret, secretFile, fileReader);
    }

    @Bean
    @ConditionalOnMissingBean(JwtParser.class)
    public JwtParser jwtParser() {
        return Jwts.parser();
    }

    @Bean
    @ConditionalOnMissingBean(JwtBuilder.class)
    public JwtBuilder jwtBuilder() {
        return Jwts.builder();
    }

    @Bean
    @ConditionalOnMissingBean(KeyPair.class)
    @Autowired
    public KeyPair keyPair(SecretKeeper secretKeeper, KeyParser keyParser) throws IOException {
        return keyParser.parse(secretKeeper.getSecret());
    }

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    public Clock clock() {
        return new DateClock();
    }

    @Bean
    @ConditionalOnMissingBean(FileReader.class)
    public FileReader fileReader() {
        return new ResourceFileReader();
    }

    @Bean
    @ConditionalOnMissingBean(KeyParser.class)
    @Autowired
    public KeyParser keyParser(Base64KeyPairs base64KeyPairs, PemKeyPairs pemKeyPairs) {
        return new SignatureAlgorithmKeyParser(algorithm, base64KeyPairs, pemKeyPairs);
    }

    @Bean
    @ConditionalOnMissingBean(Base64KeyPairs.class)
    @Autowired
    public Base64KeyPairs base64KeyPairs(Base64 base64) {
        return new SecretBase64KeyPairs(base64, algorithm);
    }

    @Bean
    @ConditionalOnMissingBean(PemKeyPairs.class)
    public PemKeyPairs pemKeyPairs() {
        return new BouncyCastlePemKeyPairs();
    }

    @Bean
    @ConditionalOnMissingBean(Base64.class)
    public Base64 base64() {
        return new DatatypeConverterBase64();
    }
}