package org.apache.coyote.util;

import static org.apache.coyote.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.HttpMethod;
import org.junit.jupiter.api.Test;

class RequestExtractorTest {

    @Test
    void 요청의_HTTP_Method를_추출한다() {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Content-Type: text/html;charset=utf-8",
                "",
                "");

        // when
        HttpMethod result = RequestExtractor.extractHttpMethod(request);

        // then
        assertThat(result).isEqualTo(GET);
    }

    @Test
    void 요청의_목표_경로를_추출한다() {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Content-Type: text/html;charset=utf-8",
                "",
                "");

        // when
        String result = RequestExtractor.extractTargetPath(request);

        // then
        assertThat(result).isEqualTo("/");
    }
}
