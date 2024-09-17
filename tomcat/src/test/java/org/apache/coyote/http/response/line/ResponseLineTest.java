package org.apache.coyote.http.response.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpProtocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("응답 라인 테스트 작성")
class ResponseLineTest {

    @Test
    @DisplayName("HttpStatus와 HttpProtocol이 설정된 ResponseLine은 올바른 메시지를 반환한다.")
    void resolveLineMessage() {
        // given
        ResponseLine responseLine = new ResponseLine(HttpStatus.OK, HttpProtocol.HTTP_11);

        // when & then
        String expectedMessage = "HTTP/1.1 200 OK";
        assertThat(responseLine.resolveLineMessage()).isEqualTo(expectedMessage);
    }
}
