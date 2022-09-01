package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @DisplayName("http request의 uri를 반환한다.")
    @Test
    void getUri() {
        // given
        List<String> httpRequestValue = Arrays.stream(String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "").split("\r\n")).collect(Collectors.toList());

        // when
        HttpRequest request = HttpRequest.from(httpRequestValue);

        // then
        assertThat(request.getUri()).isEqualTo("/index.html");
    }
}
