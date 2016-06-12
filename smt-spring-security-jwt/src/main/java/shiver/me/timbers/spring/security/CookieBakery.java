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

import javax.servlet.http.Cookie;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Bennett
 */
public class CookieBakery implements Bakery<Cookie> {

    private final int maxAgeDuration;
    private final TimeUnit maxAgeUnit;
    private final String domain;
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;

    public CookieBakery(
        int maxAgeDuration,
        TimeUnit maxAgeUnit,
        String domain,
        String path,
        boolean secure,
        boolean httpOnly
    ) {
        this.maxAgeDuration = maxAgeDuration;
        this.maxAgeUnit = maxAgeUnit;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    @Override
    public Cookie bake(String name, String value) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge((int) (maxAgeDuration >= 0 ? maxAgeUnit.toSeconds(maxAgeDuration) : maxAgeDuration));
        if (domain != null && !domain.isEmpty()) {
            cookie.setDomain(domain);
        }
        cookie.setPath(path);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }
}
