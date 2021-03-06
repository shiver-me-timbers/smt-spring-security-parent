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

package shiver.me.timbers.spring.security.jwt;

/**
 * @author Karl Bennett
 */
public class JJwtTokenParser<T> implements JwtTokenParser<T, String> {

    private final Class<T> type;
    private final JwtEncryptor encryptor;
    private final JwtDecryptor decryptor;

    public JJwtTokenParser(Class<T> type, JwtEncryptor encryptor, JwtDecryptor decryptor) {
        this.type = type;
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }

    @Override
    public String create(T principal) {
        return encryptor.encrypt(principal);
    }

    @Override
    public T parse(String token) {
        return decryptor.decrypt(token, type);
    }
}
