package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.apache.coyote.http11.response.HttpContentType.TEXT_HTML_UTF8;
import static org.apache.coyote.http11.response.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpHeader.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseHeaderTest {

    @Test
    void 모든_header의_field와_value에_관한_메시지를_얻는다() {
        // given
        ResponseHeader responseHeader = new ResponseHeader(Map.of(
                CONTENT_TYPE, TEXT_HTML_UTF8.contentTypeName(),
                CONTENT_LENGTH, "5"
        ));

        // when
        List<String> headers = responseHeader.headerMessages();

        // then
        assertThat(headers).containsExactly(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5"
        );
    }

}
