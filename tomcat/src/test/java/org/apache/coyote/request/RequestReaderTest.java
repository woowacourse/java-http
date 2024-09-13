package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class RequestReaderTest {
    @Test // TODO: RequestReader 클래스의 readRequest 메서드를 테스트하는 코드를 작성하라.
    void 요청을_읽는다() {
        // given
        String mockHttpRequest = "GET /index.html HTTP/1.1\r\n" +
                                 "Host: localhost\r\n" +
                                 "User-Agent: Test\r\n" +
                                 "\r\n" +
                                 "account=gugu";  // HTTP 헤더 끝에 바디가 붙음

        ByteArrayInputStream inputStream = new ByteArrayInputStream(mockHttpRequest.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest httpRequest = RequestReader.readRequest(inputStream);

        // then
        assertAll(
                () -> assertThat(httpRequest.getPath()).isEqualTo("/index"),
                () -> assertThat(httpRequest.getBody().get("account")).isEqualTo("gugu")
        );
    }
}
