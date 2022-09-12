package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("uri에 쿼리 스트링이 포함되어 있으면 false를 반환한다.")
    @Test
    void isExistQueryString_false() {
        //given
        final String requestLine = "GET /index.html HTTP/1.1";
        RequestLine requestUri = RequestLine.from(requestLine);

        //when, then
        assertThat(requestUri.isExistQueryString()).isFalse();
    }

    @DisplayName("uri에 쿼리 스트링이 포함되어 있으면 true를 반환한다.")
    @Test
    void isExistQueryString_true() {
        //given
        final String requestLine = "GET http://localhost:8080/login?account=gugu&password=password HTTP/1.1";
        RequestLine requestUri = RequestLine.from(requestLine);
        System.out.println("requestUri = " + requestUri.getRequestUri());
        //when, then
        assertThat(requestUri.isExistQueryString()).isTrue();
    }

    @DisplayName("쿼리 스트링 키에 대한 값을 얻을 수 있다.")
    @Test
    void getQueryStringValue() {
        //given
        final String requestLine = "GET http://localhost:8080/login?account=gugu&password=password HTTP/1.1 ";
        RequestLine requestUri = RequestLine.from(requestLine);

        //when
        final String account = requestUri.getQueryStringValue("account");
        final String password = requestUri.getQueryStringValue("password");

        //then
        assertThat(account).isEqualTo("gugu");
        assertThat(password).isEqualTo("password");
    }
}
