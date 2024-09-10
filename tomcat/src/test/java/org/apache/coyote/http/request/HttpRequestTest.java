package org.apache.coyote.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    private static final String request = String.join(
            "\r\n",
            "GET / HTTP/1.1",
            "Cookie:JSESSIONID=abcdef;cake=delicious",
            "Content-Length:12");
    private static final HttpRequest httpRequest = HttpRequest.of(request);

    @Test
    @DisplayName("null 이거나 빈 문자열의 경우 예외를 발생한다.")
    void validate() {
        String wrong = "";
        List<String> wrongList = new ArrayList<>();
        assertThatThrownBy(() -> HttpRequest.of(wrong))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> HttpRequest.of(wrongList))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
