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

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import org.msgpack.MessagePack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shiver.me.timbers.spring.security.Base64;
import shiver.me.timbers.spring.security.jwt.AuthenticationConverter;
import shiver.me.timbers.spring.security.jwt.JwtTokenParser;
import shiver.me.timbers.spring.security.jwt.MsgPackJwtTokenParser;
import shiver.me.timbers.spring.security.time.Clock;

import java.security.KeyPair;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.spy;

@Configuration
public class JwtCustomPrincipleConfiguration {

    @Value("${smt.spring.security.jwt.algorithm:HS512}")
    private SignatureAlgorithm algorithm;

    @Value("${smt.spring.security.jwt.token.expiryDuration:-1}")
    private int expiryDuration;

    @Value("${smt.spring.security.jwt.token.expiryUnit:MINUTES}")
    private TimeUnit expiryUnit;

    @Bean
    public AuthenticationConverter<CustomPrincipal> authenticationConverter() {
        return spy(new CustomPrincipleAuthenticationConverter());
    }

    @Bean
    @Autowired
    public JwtTokenParser<CustomPrincipal, String> jwtTokenParser(
        JwtBuilder builder,
        JwtParser parser,
        KeyPair keyPair,
        Clock clock,
        MessagePack messagePack,
        Base64 base64
    ) {
        return new MsgPackJwtTokenParser<>(
            CustomPrincipal.class,
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
    public MessagePack messagePack() {
        final MessagePack messagePack = new MessagePack();
        messagePack.register(CustomPrincipal.class);
        return messagePack;
    }
}
