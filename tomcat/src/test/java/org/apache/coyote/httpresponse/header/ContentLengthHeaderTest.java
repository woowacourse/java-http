package org.apache.coyote.httpresponse.header;

import org.apache.coyote.httpresponse.ContentBody;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class ContentLengthHeaderTest {

    @Test
    void 올바른_형식의_헤더로_응답한다() {
        // given
        final String content = "화이팅입니다 베베~";
        final ContentBody contentBody = new ContentBody(content);

        // when
        final ContentLengthHeader contentLengthHeader = new ContentLengthHeader(contentBody);
        final String actual = contentLengthHeader.getKeyAndValue(ResponseHeaderType.CONTENT_LENGTH);
        final int contentLength = content.getBytes().length;
        final String expected = String.format("Content-Length: %d", contentLength);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
