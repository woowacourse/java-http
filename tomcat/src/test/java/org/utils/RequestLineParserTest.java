package org.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.common.request.parser.RequestLineParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RequestLineParser 는 ")
class RequestLineParserTest {

    @DisplayName("요청 시작줄이 주어지면 시작줄에 존재하는 url을 가져온다.")
    @Test
    void getUrlFromRequestLine() {
        final String requestLine = "GET /index.html HTTP/1.1";

        final String actual = RequestLineParser.getStaticResourcePath(requestLine);

        assertThat(actual).isEqualTo("/index.html");
    }
}
