package org.apache.coyote.http11.web.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.support.HttpHeader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStartLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.LinkedHashMap;

class HttpRequestParserTest {

    @DisplayName("HTTP 요청을 파싱한다.")
    @Test
    void execute() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive", "Accept: text/html");
        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final HttpStartLine httpStartLine = HttpStartLine.from(new String[]{"GET", "/index.html", "HTTP/1.1"});
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.HOST, "localhost:8080");
        httpHeaders.put(HttpHeader.CONNECTION, "keep-alive");
        httpHeaders.put(HttpHeader.ACCEPT, "text/html");
        final HttpRequest expected = new HttpRequest(httpStartLine, httpHeaders, "");

        // when
        final HttpRequest actual = HttpRequestParser.execute(bufferedReader);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
