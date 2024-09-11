package org.apache.catalina.controller.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("요청의 uri를 반환한다.")
    void getRequestURI() {
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET, "/login/hello?account=account&password=password", "HTTP/1.1"
        );

        assertThat(requestLine.getRequestURI()).isEqualTo("/login/hello");
    }

    @Test
    @DisplayName("url의 쿼리 파라미터를 Map 형태로 반환한다.")
    void getQueryParameters() {
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET, "/login/hello?account=account&password=password", "HTTP/1.1"
        );

        assertThat(requestLine.getQueryParameters())
                .containsOnly(
                        entry("account", new String[]{"account"}),
                        entry("password", new String[]{"password"}));
    }
}
