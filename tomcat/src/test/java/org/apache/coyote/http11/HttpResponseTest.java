package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 응답 테스트")
class HttpResponseTest {

    @DisplayName("HTTP 응답 생성에 성공한다.")
    @Test
    void toByte() {
        // given
        HttpStateCode stateCode = HttpStateCode.OK;
        String bodyContent = "<html><body>Hello, World!</body></html>";
        byte[] body = bodyContent.getBytes(StandardCharsets.UTF_8);

        HttpResponse httpResponse = new HttpResponse(stateCode, MimeType.HTML, body);

        // when
        byte[] responseBytes = httpResponse.toByte();
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);

        // then
        assertAll(
                () -> assertThat(responseString).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(responseString).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(responseString).contains("Content-Length: " + body.length),
                () -> assertThat(responseString).endsWith(bodyContent)
        );
    }
}
