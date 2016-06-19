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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import shiver.me.timbers.spring.security.weaving.Weaver;

import javax.annotation.PostConstruct;
import java.security.Security;

/**
 * @author Karl Bennett
 */
@Configuration
@ConditionalOnMissingBean(JwtSpringSecurityConfiguration.class)
@Import({JwtConfiguration.class, JwtModificationConfiguration.class, JwtWeavingConfiguration.class})
public class JwtSpringSecurityConfiguration {

    @Autowired
    private Weaver weaver;

    @PostConstruct
    public void configure() {
        Security.addProvider(new BouncyCastleProvider()); // Enable support for all the hashing algorithms.
        weaver.weave();
    }
}