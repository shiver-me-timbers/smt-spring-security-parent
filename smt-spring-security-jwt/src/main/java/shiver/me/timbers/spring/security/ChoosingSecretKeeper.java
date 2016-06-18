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

import java.io.IOException;

/**
 * @author Karl Bennett
 */
public class ChoosingSecretKeeper implements SecretKeeper {

    private final String secret;
    private final String secretFile;
    private final FileReader fileReader;

    public ChoosingSecretKeeper(String secret, String secretFile, FileReader fileReader) {
        if (secret.isEmpty() && secretFile.isEmpty()) {
            throw new IllegalArgumentException(
                "At least one of either (smt.spring.security.jwt.secret) or (smt.spring.security.jwt.secretFile) must be set."
            );
        }
        this.secret = secret;
        this.secretFile = secretFile;
        this.fileReader = fileReader;
    }

    @Override
    public String getSecret() {
        if (!secret.isEmpty()) {
            return secret;
        }
        try {
            return fileReader.read(secretFile);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
