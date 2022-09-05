package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("filSource를 반환한다.")
    @Test
    void getFileSource() {
        String request = "GET /index.html HTTP/1.1";
        HttpRequest httpRequest = HttpRequest.from(request);
        String expected = "/index.html";

        String actual = httpRequest.getFileSource();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("쿼리스트링을 포함한 요청에서 filSource를 반환한다.")
    @Test
    void getFileSource_IncludingQueryString() {
        String request = "GET /login?account=gugu&password=password HTTP/1.1";
        HttpRequest httpRequest = HttpRequest.from(request);
        String expected = "/login";

        String actual = httpRequest.getFileSource();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("쿼리스트링을 포함한 요청에서 쿼리파라미터의 값을 반환한다.")
    @Test
    void getQueryParamValueOf() {
        String request = "GET /login?account=gugu&password=password HTTP/1.1";
        HttpRequest httpRequest = HttpRequest.from(request);

        String accountValue = httpRequest.getQueryParamValueOf("account");
        String passwordValue = httpRequest.getQueryParamValueOf("password");

        assertAll(
                () -> assertThat(accountValue).isEqualTo("gugu"),
                () -> assertThat(passwordValue).isEqualTo("password")
        );
    }
}