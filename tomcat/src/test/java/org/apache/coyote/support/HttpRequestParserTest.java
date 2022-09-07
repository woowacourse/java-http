package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import org.apache.coyote.exception.InvalidHttpRequestFormatException;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @Test
    void 헤더_파싱_테스트() {
        // given
        List<String> requestLines = List.of("GET /index.html HTTP/1.1", "Accept: */*", "Accept-Language: ko",
                "Accept-Encoding: gzip, deflate", "If-Modified-Since: Fri, 21 Jul 2006 05:31:13 GMT",
                "If-None-Match: \"734237e186acc61:a1b\"",
                "User-Agent: Mozilla/4.0(compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; InfoPath.1)",
                "Host: localhost", "Connection: Keep-Alive");

        // when
        Map<String, String> headers = HttpRequestParser.parseHeaders(requestLines);

        // then
        assertAll(
                () -> assertThat(headers).hasSize(8),
                () -> assertThat(headers.keySet()).containsOnly("Accept", "Accept-Language", "Accept-Encoding",
                        "If-Modified-Since", "If-None-Match", "User-Agent", "Host", "Connection")
        );
    }

    @Test
    void 바디_파싱_테스트() {
        // given
        List<String> requestLines = List.of("GET /index.html HTTP/1.1", "Accept: */*", "Accept-Language: ko",
                "Accept-Encoding: gzip, deflate", "If-Modified-Since: Fri, 21 Jul 2006 05:31:13 GMT",
                "If-None-Match: \"734237e186acc61:a1b\"",
                "User-Agent: Mozilla/4.0(compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; InfoPath.1)",
                "Host: localhost", "Connection: Keep-Alive",
                "",
                "requestBody1", "requestBody2");

        // when
        String requestBody = HttpRequestParser.parseBody(requestLines);

        // then
        assertThat(requestBody).isEqualTo("requestBody1" + System.lineSeparator() + "requestBody2");
    }

    @Test
    void 형식이_올바르지_않으면_예외를_반환한다() {
        // given
        String line = "invalid";
        List<String> lines = List.of("invalid", "invalid", "invalid");

        // when, then
        assertThatThrownBy(() -> HttpRequestParser.parseHeaders(lines))
                .isExactlyInstanceOf(InvalidHttpRequestFormatException.class);
    }

    @Test
    void 확장자_가져오기_테스트() {
        // given
        String fileName = "nextstep.txt";

        // when
        String actual = HttpRequestParser.parseExtension(fileName);

        // then
        assertThat(actual).isEqualTo("txt");
    }

    @Test
    void 확장자가_없으면_빈_문자열을_반환한다() {
        // given
        String fileName = "no_extension";

        // when
        String actual = HttpRequestParser.parseExtension(fileName);

        // then
        assertThat(actual).isBlank();
    }
}
