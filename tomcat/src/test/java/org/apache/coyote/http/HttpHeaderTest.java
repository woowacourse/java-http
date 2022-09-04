package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @Test
    @DisplayName("from 정적 팩토리 메서드는 http 요청 메시지의 header를 파싱해서 Map에 저장한다.")
    void from() throws IOException {
        // given
        final int contentLength = 999;
        final String sessionId = "1q2w3e4r";

        final String httpMessage = String.join("\r\n",
                "Content-Length: " + contentLength + " ",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // when
        final HttpHeader actual = HttpHeader.from(bufferedReader);

        inputStream.close();

        // then
        assertAll(
                () -> assertThat(actual.getContentLength()).isEqualTo(contentLength),
                () -> assertThat(actual.getSessionId()).isEqualTo(sessionId)
        );
    }
}
