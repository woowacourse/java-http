package org.apache.coyote.http11.request;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {
    List<String> validGetRequest = List.of(
            "GET / HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive "
    );
    List<String> invalidGetRequest = List.of(
            "GETO /// HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive "
    );

    @Test
    @DisplayName("유효하지 않은 request line이라면 예외가 발생한다")
    void invalidRequestLine() {
        Assertions.assertThatCode(()->HttpRequestLine.from(validGetRequest))
                .doesNotThrowAnyException();
        Assertions.assertThatThrownBy(()->HttpRequestLine.from(invalidGetRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
