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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.matchers.Matchers.hasField;

public class AuthenticationRequestJwtTokenParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private String tokenName;
    private AuthenticationRequestJwtTokenParser tokenParser;
    private JwtTokenParser<String, String> principleTokenParser;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        tokenName = someString();
        principleTokenParser = mock(JwtTokenParser.class);
        tokenParser = new AuthenticationRequestJwtTokenParser(tokenName, principleTokenParser);
    }

    @Test
    public void Can_parse_a_jwt_token_from_a_cookie() throws JwtInvalidTokenException {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        final Cookie cookie = mock(Cookie.class);
        final String token = someString();
        final String principle = someString();

        // Given
        given(request.getCookies()).willReturn(new Cookie[]{mock(Cookie.class), cookie, mock(Cookie.class)});
        given(cookie.getName()).willReturn(tokenName);
        given(cookie.getValue()).willReturn(token);
        given(principleTokenParser.parse(token)).willReturn(principle);

        // When
        final Authentication actual = tokenParser.parse(request);

        // Then
        assertThat(actual, hasField("principle", principle));
        assertThat(actual, hasField("authenticated", true));
    }

    @Test
    public void Can_parse_a_jwt_token_from_a_header() throws JwtInvalidTokenException {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        final String token = someString();
        final String principle = someString();

        // Given
        given(request.getHeader(tokenName)).willReturn(token);
        given(principleTokenParser.parse(token)).willReturn(principle);

        // When
        final Authentication actual = tokenParser.parse(request);

        // Then
        assertThat(actual, hasField("principle", principle));
        assertThat(actual, hasField("authenticated", true));
    }
}