package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.LinkedList;
import java.util.Queue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("정적 팩토리 메소드는 입력을 파싱해서 RequestLine과 Header로 저장한다.")
    void setUp() {
        // given
        final Queue<String> rawRequest = new LinkedList<>();
        rawRequest.add("GET /path?id=yujeong HTTP/1.1");
        rawRequest.add("name: eve");

        // when
        final HttpRequest httpRequest = HttpRequest.of(rawRequest);

        // then
        assertAll(() -> {
                    assertThat(httpRequest.getPath()).isEqualTo("/path");
                    assertThat(httpRequest.getQueryParams())
                            .extractingByKey("id").isEqualTo("yujeong");
                    assertThat(httpRequest.getHeader("name")).isEqualTo("eve");
                }
        );
    }
}