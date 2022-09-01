package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class HttpRequestTest {

    @DisplayName("http request의 path를 반환한다.")
    @ParameterizedTest
    @CsvSource({"/index.html,/index.html", "/index.html?name=value,/index.html"})
    void getPath(String uri, String expected) {
        // given
        List<String> httpRequestValue = Arrays.stream(String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "").split("\r\n")).collect(Collectors.toList());

        // when
        HttpRequest request = HttpRequest.from(httpRequestValue);

        // then
        assertThat(request.path()).isEqualTo(expected);
    }

    @DisplayName("query string을 반환한다.")
    @Test
    void queryString() {
        // given
        List<String> httpRequestValue = Arrays.stream(String.join("\r\n",
                "GET /index.html?account=tonic&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "").split("\r\n")).collect(Collectors.toList());

        // when
        HttpRequest request = HttpRequest.from(httpRequestValue);
        QueryString queryString = request.queryString();
        // then
        assertAll(
                () -> assertThat(queryString.get("account")).isEqualTo("tonic"),
                () -> assertThat(queryString.get("password")).isEqualTo("password")
        );
    }
}
