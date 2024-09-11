package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    @DisplayName("성공 : body 값 추가 가능")
    void setBody() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());
        Map<String, String> expected = Map.of("key", "value");

        request.setBody(expected);

        Map<String, String> actual = request.getBody();
        assertThat(actual).isEqualTo(expected);
    }
}
