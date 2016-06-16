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

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;

/**
 * @author Karl Bennett
 */
public class BouncyCastlePemKeyPairs implements PemKeyPairs {

    @Override
    public KeyPair createPair(String secret) throws IOException {
        final PEMParser pemParser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(secret.getBytes())));
        return new JcaPEMKeyConverter().setProvider("BC").getKeyPair((PEMKeyPair) pemParser.readObject());
    }
}
