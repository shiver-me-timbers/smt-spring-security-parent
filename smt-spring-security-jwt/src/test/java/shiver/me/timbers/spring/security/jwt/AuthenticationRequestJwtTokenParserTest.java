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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class AuthenticationRequestJwtTokenParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private String tokenName;
    private AuthenticationConverter<Object> authenticationConverter;
    private JwtTokenParser<Object, String> principleTokenParser;
    private AuthenticationRequestJwtTokenParser tokenParser;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        tokenName = someString();
        authenticationConverter = mock(AuthenticationConverter.class);
        principleTokenParser = mock(JwtTokenParser.class);
        tokenParser = new AuthenticationRequestJwtTokenParser<>(
            tokenName,
            authenticationConverter,
            principleTokenParser
        );
    }

    @Test
    public void Can_create_a_jwt_token_from_an_authentication() throws JwtInvalidTokenException {

        final Authentication authentication = mock(Authentication.class);

        final Object principle = new Object();

        final String expected = someString();

        // Given
        given(authenticationConverter.convert(authentication)).willReturn(principle);
        given(principleTokenParser.create(principle)).willReturn(expected);

        // When
        final String actual = tokenParser.create(authentication);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_parse_a_jwt_token_from_a_cookie() throws JwtInvalidTokenException {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        final Cookie cookie = mock(Cookie.class);
        final String token = someString();
        final Object principle = new Object();
        final Authentication expected = mock(Authentication.class);

        // Given
        given(request.getCookies()).willReturn(new Cookie[]{mock(Cookie.class), cookie, mock(Cookie.class)});
        given(cookie.getName()).willReturn(tokenName);
        given(cookie.getValue()).willReturn(token);
        given(principleTokenParser.parse(token)).willReturn(principle);
        given(authenticationConverter.convert(principle)).willReturn(expected);

        // When
        final Authentication actual = tokenParser.parse(request);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_parse_a_jwt_token_from_a_header() throws JwtInvalidTokenException {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        final String token = someString();
        final Object principle = new Object();
        final Authentication expected = mock(Authentication.class);

        // Given
        given(request.getCookies()).willReturn(new Cookie[0]);
        given(request.getHeader(tokenName)).willReturn(token);
        given(principleTokenParser.parse(token)).willReturn(principle);
        given(authenticationConverter.convert(principle)).willReturn(expected);

        // When
        final Authentication actual = tokenParser.parse(request);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_to_find_a_jwt_token() throws JwtInvalidTokenException {

        final HttpServletRequest request = mock(HttpServletRequest.class);

        final JwtInvalidTokenException exception = new JwtInvalidTokenException(new Exception());

        // Given
        given(request.getCookies()).willReturn(null);
        given(request.getHeader(tokenName)).willReturn(null);
        given(principleTokenParser.parse("")).willThrow(exception);
        expectedException.expect(is(exception));

        // When
        tokenParser.parse(request);
    }
}