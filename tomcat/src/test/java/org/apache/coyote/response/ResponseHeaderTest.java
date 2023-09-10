package org.apache.coyote.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.response.header.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.response.header.HttpHeader.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.apache.coyote.response.header.HttpContentType;
import org.apache.coyote.response.header.ResponseHeader;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseHeaderTest {

    @Test
    void 모든_header의_field와_value에_관한_메시지를_얻는다() {
        // given
        ResponseHeader responseHeader = new ResponseHeader(
                Map.of(
                        CONTENT_TYPE, HttpContentType.TEXT_HTML.mimeTypeWithCharset(UTF_8),
                        CONTENT_LENGTH, "5"
                )
        );

        // when
        List<String> headers = responseHeader.headerMessages();

        // then
        assertThat(headers).contains(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5"
        );
    }

}
