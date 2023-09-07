package org.apache.coyote.httpresponse.header;

import org.apache.coyote.httpresponse.ContentBody;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class ResponseHeadersTest {

    @Test
    void 필수_응답_헤더_생성에_성공한다() {
        // given
        final String path = "/index.html";
        final String content = "hello world!";
        final ContentBody contentBody = new ContentBody(content);

        // when
        final ResponseHeaders responseHeaders = ResponseHeaders.of(path, contentBody);
        final String actual = responseHeaders.getFormattedHeaders();
        final String expected = String.join("\r\n",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "");

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void Location_헤더_세팅에_성공한다() {
        // given
        final String path = "/login.html";
        final String content = "hello world!";
        final ContentBody contentBody = new ContentBody(content);
        final String locationHeader = "/index.html";

        // when
        final ResponseHeaders responseHeaders = ResponseHeaders.of(path, contentBody);
        responseHeaders.setLocationHeader(locationHeader);
        final String actual = responseHeaders.getFormattedHeaders();
        final String expected = String.join("\r\n",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "Location: /index.html ",
                "");

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
