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
import org.msgpack.MessagePack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import shiver.me.timbers.spring.security.context.SecurityContextHolder;
import shiver.me.timbers.spring.security.context.StaticSecurityContextHolder;
import shiver.me.timbers.spring.security.cookies.Bakery;
import shiver.me.timbers.spring.security.cookies.CookieBakery;
import shiver.me.timbers.spring.security.io.FileReader;
import shiver.me.timbers.spring.security.io.ResourceFileReader;
import shiver.me.timbers.spring.security.jwt.AuthenticationJwtPrincipleConverter;
import shiver.me.timbers.spring.security.jwt.AuthenticationRequestJwtTokenParser;
import shiver.me.timbers.spring.security.jwt.GrantedAuthorityConverter;
import shiver.me.timbers.spring.security.jwt.JwtPrinciple;
import shiver.me.timbers.spring.security.jwt.JwtPrincipleConverter;
import shiver.me.timbers.spring.security.jwt.JwtTokenParser;
import shiver.me.timbers.spring.security.jwt.MsgPackJwtTokenParser;
import shiver.me.timbers.spring.security.jwt.RolesGrantedAuthorityConverter;
import shiver.me.timbers.spring.security.keys.Base64KeyPairs;
import shiver.me.timbers.spring.security.keys.BouncyCastlePemKeyPairs;
import shiver.me.timbers.spring.security.keys.KeyParser;
import shiver.me.timbers.spring.security.keys.PemKeyPairs;
import shiver.me.timbers.spring.security.keys.SecretBase64KeyPairs;
import shiver.me.timbers.spring.security.keys.SignatureAlgorithmKeyParser;
import shiver.me.timbers.spring.security.secret.ChoosingSecretKeeper;
import shiver.me.timbers.spring.security.secret.SecretKeeper;
import shiver.me.timbers.spring.security.time.Clock;
import shiver.me.timbers.spring.security.time.DateClock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.KeyPair;
import java.util.List;
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
        JwtPrincipleConverter<Authentication> jwtPrincipleConverter,
        JwtTokenParser<JwtPrinciple, String> principleJwtTokenParser
    ) {
        return new AuthenticationRequestJwtTokenParser(tokenName, jwtPrincipleConverter, principleJwtTokenParser);
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
    @ConditionalOnMissingBean(JwtPrincipleConverter.class)
    @Autowired
    public JwtPrincipleConverter<Authentication> jwtPrincipleConverter(
        GrantedAuthorityConverter<List<String>> grantedAuthorityConverter
    ) {
        return new AuthenticationJwtPrincipleConverter(grantedAuthorityConverter);
    }

    @Bean
    @ConditionalOnMissingBean(MsgPackJwtTokenParser.class)
    @Autowired
    public JwtTokenParser<JwtPrinciple, String> principleJwtTokenParser(
        JwtBuilder builder,
        JwtParser parser,
        KeyPair keyPair,
        Clock clock,
        MessagePack messagePack,
        Base64 base64
    ) {
        return new MsgPackJwtTokenParser<>(
            JwtPrinciple.class,
            builder,
            parser,
            algorithm,
            keyPair,
            expiryDuration,
            expiryUnit,
            clock,
            messagePack,
            base64
        );
    }

    @Bean
    @ConditionalOnMissingBean(GrantedAuthorityConverter.class)
    public GrantedAuthorityConverter<List<String>> grantedAuthorityConverter() {
        return new RolesGrantedAuthorityConverter();
    }

    @Bean
    @ConditionalOnMissingBean(JwtBuilder.class)
    public JwtBuilder jwtBuilder() {
        return Jwts.builder();
    }

    @Bean
    @ConditionalOnMissingBean(JwtParser.class)
    public JwtParser jwtParser() {
        return Jwts.parser();
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
    @ConditionalOnMissingBean(MessagePack.class)
    public MessagePack messagePack() {
        final MessagePack messagePack = new MessagePack();
        messagePack.register(JwtPrinciple.class);
        return messagePack;
    }

    @Bean
    @ConditionalOnMissingBean(SecretKeeper.class)
    public SecretKeeper secretKeeper(FileReader fileReader) {
        return new ChoosingSecretKeeper(secret, secretFile, fileReader);
    }

    @Bean
    @ConditionalOnMissingBean(KeyParser.class)
    @Autowired
    public KeyParser keyParser(Base64KeyPairs base64KeyPairs, PemKeyPairs pemKeyPairs) {
        return new SignatureAlgorithmKeyParser(algorithm, base64KeyPairs, pemKeyPairs);
    }

    @Bean
    @ConditionalOnMissingBean(FileReader.class)
    public FileReader fileReader() {
        return new ResourceFileReader();
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