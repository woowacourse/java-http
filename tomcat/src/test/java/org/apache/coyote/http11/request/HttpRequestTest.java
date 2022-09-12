package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;

import org.apache.catalina.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import support.TestRequest;

class HttpRequestTest {

    @ParameterizedTest
    @CsvSource(value = {"GET:true", "POST:false"}, delimiter = ':')
    @DisplayName("GET 요청인지 검증한다.")
    void isGetRequest(final String method, final boolean expected) {
        // given
        final HttpRequest request = TestRequest.generateWithRequestLine(method, "/path", "HTTP/1.1");

        // when
        final boolean actual = request.isGetRequest();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"/:true", "/path:false"}, delimiter = ':')
    @DisplayName("Root path 요청인지 검증한다.")
    void isRootPath(final String path, final boolean expected) {
        // given
        final HttpRequest request = TestRequest.generateWithRequestLine("GET", path, "HTTP/1.1");

        // when
        final boolean actual = request.isRootPath();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Path 반환 값을 검증한다.")
    void getPath() {
        // given
        final String path = "/path";
        final HttpRequest request = TestRequest.generateWithRequestLine("GET", path, "HTTP/1.1");

        // when
        final String actual = request.getPath();

        // then
        assertThat(actual).isEqualTo(path);
    }

    @ParameterizedTest
    @CsvSource(value = {"Host:true", "Accept:false"}, delimiter = ':')
    @DisplayName("Header를 포함하는지 검증한다.")
    void containsHeader(final String headerName, final boolean expected) {
        // given
        final HttpRequest request = TestRequest.generateWithRequestLine("GET", "/path", "HTTP/1.1");

        // when
        final boolean actual = request.containsHeader(headerName);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Session을 포함하는지 검증한다(true).")
    void hasSession_true() {
        // given
        final HttpRequest request = TestRequest.generateWithSession("GET", "/path", "HTTP/1.1",
            "JSESSIONID=testId");

        // when
        final boolean actual = request.hasSession();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Session을 포함하는지 검증한다(false).")
    void hasSession_false() {
        // given
        final HttpRequest request = TestRequest.generateWithRequestLine("GET", "/path", "HTTP/1.1");

        // when
        final boolean actual = request.hasSession();

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Session 값을 검증한다.")
    void getSession() {
        // given
        final HttpRequest request = TestRequest.generateWithSession("GET", "/path", "HTTP/1.1",
            "JSESSIONID=testId");

        // when
        final Session session = request.getSession();
        final String actual = session.getId();

        // then
        assertThat(actual).isEqualTo("testId");
    }

    @Test
    @DisplayName("Session 조회시 Session 값이 없으면 예외를 반환한다.")
    void getSession_exception() {
        // given
        final HttpRequest request = TestRequest.generateWithRequestLine("GET", "/path", "HTTP/1.1");

        // when, then
        assertThatThrownBy(request::getSession).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("JSESSIONID Not Found");
    }
}
