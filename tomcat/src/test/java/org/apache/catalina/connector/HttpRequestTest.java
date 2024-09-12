package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.apache.tomcat.http.common.Method;
import org.apache.tomcat.http.common.Version;
import org.apache.tomcat.http.common.body.FormUrlEncodeBody;
import org.apache.tomcat.http.common.body.TextTypeBody;
import org.apache.tomcat.http.request.RequestHeaders;
import org.apache.tomcat.http.request.RequestLine;
import org.apache.tomcat.http.response.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpRequestTest {

    @ParameterizedTest
    @DisplayName("같은 메서드 인지 확인한다.")
    @EnumSource(Method.class)
    void check_same_method(final Method method) {
        // given
        final var requestLine = new RequestLine(method, URI.create("/"), new Version(1, 1));
        final var requestHeaders = new RequestHeaders("");
        final var textTypeBody = new TextTypeBody("");
        final var cookie = new Cookie("");
        final var httpRequest = new HttpRequest(requestLine, requestHeaders, textTypeBody, cookie);

        // when
        final var sameMethod = httpRequest.isSameMethod(method);

        // then
        assertThat(sameMethod).isTrue();
    }

    @Test
    @DisplayName("쿠키를 찾아온다")
    void get_cookie_value() {
        // given
        final var requestLine = new RequestLine(Method.GET, URI.create("/"), new Version(1, 1));
        final var requestHeaders = new RequestHeaders("");
        final var textTypeBody = new TextTypeBody("");
        final var cookie = new Cookie("Cookie: name=fram");
        final var httpRequest = new HttpRequest(requestLine, requestHeaders, textTypeBody, cookie);

        // when
        final var cookieContent = httpRequest.getCookieContent("name");

        // then
        assertThat(cookieContent).isEqualTo("fram");
    }

    @Test
    @DisplayName("바디에서 값을 가져온다.")
    void get_body_value() {
        // given
        final var requestLine = new RequestLine(Method.GET, URI.create("/"), new Version(1, 1));
        final var requestHeaders = new RequestHeaders("");
        final var textTypeBody = new FormUrlEncodeBody("name=hello&password=pp");
        final var cookie = new Cookie("Cookie: name=fram");
        final var httpRequest = new HttpRequest(requestLine, requestHeaders, textTypeBody, cookie);

        // when
        final var bodyContent = httpRequest.getBodyContent("name");

        // then
        assertThat(bodyContent).isEqualTo("hello");
    }
}
