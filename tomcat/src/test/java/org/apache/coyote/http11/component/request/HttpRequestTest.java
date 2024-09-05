package org.apache.coyote.http11.component.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("헤더를 평문에서 추출한다.")
    void extract_header_info() {
        // given
        final var plaintext = """
                PUT /update-user HTTP/1.1
                Host: example.com
                Content-Type: application/json
                Content-Length: 43
                                
                {
                  "name": "JohnDoe",
                  "age": 25
                }
                """;

        // when
        final var request = new HttpRequest(plaintext);

        // then
        assertThat(request.getHeaders().get("host")).isEqualTo("example.com");
    }
}
