package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.request.spec.StartLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName(" Content-Type이 x-www-form-urlencoded가 아니면 parameters는 빈 맵이다.")
    void emptyParams() {
        HttpRequest request = new HttpRequest(StartLine.from("POST /login HTTP/1.1\r\n"),
                HttpHeaders.from(List.of("Content-Type: application/json")));

        assertThat(request.getParameter("gugu")).isNull();
    }

    @Test
    @DisplayName(" Content-Type이 x-www-form-urlencoded이면 body를 paramerters로 파싱한다.")
    void parseParams() {
        HttpRequest request = new HttpRequest(StartLine.from("POST /login HTTP/1.1\r\n"),
                HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded")),
                "account=gugu&password=password");

        assertAll(
                () -> assertThat(request.getParameter("account")).isEqualTo("gugu"),
                () -> assertThat(request.getParameter("password")).isEqualTo("password")
        );
    }
}
