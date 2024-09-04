package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 헤더 테스트")
class HttpHeaderTest {

    @DisplayName("HTTP 헤더 생성에 성공한다.")
    @Test
    void contentLength() {
        // given
        String contentLength = "12";
        MimeType mimeType = MimeType.HTML;
        String expected = String.join("\r\n",
                "Content-Length: 12 ",
                "Content-Type: " + mimeType.getContentType() + " "
        );
        HttpHeader header = new HttpHeader();

        // when
        header.setContentLength(contentLength);
        header.setContentType(mimeType.getContentType());

        // then
        assertThat(header.toHeaderString()).isEqualTo(expected);
    }
}
