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

import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaNumericString;
import static shiver.me.timbers.matchers.Matchers.hasField;
import static shiver.me.timbers.matchers.Matchers.hasFieldThat;

public class CookieJwtLogoutHandlerTest {

    @Test
    public void Can_remove_the_jwt_cookie() {

        // Given
        final String tokenName = someAlphaNumericString(8);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        // When
        new CookieJwtLogoutHandler(tokenName)
            .logout(mock(HttpServletRequest.class), response, mock(Authentication.class));

        // Then
        verify(response).addCookie(argThat(allOf(
            (Matcher<? super Cookie>) hasField("name", tokenName),
            hasFieldThat("value", isEmptyString()),
            hasField("maxAge", 0)
        )));
    }
}