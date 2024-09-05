package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.MimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("응답 헤더 테스트")
class ResponseHeaderTest {

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
        ResponseHeader header = new ResponseHeader();

        // when
        header.setContentLength(contentLength);
        header.setContentType(mimeType);

        // then
        assertThat(header.toHeaderString()).isEqualTo(expected);
    }
}
