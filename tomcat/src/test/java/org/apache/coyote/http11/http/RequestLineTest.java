package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.http.domain.HttpMethod;
import org.apache.coyote.http11.http.domain.HttpVersion;
import org.apache.coyote.http11.http.domain.RequestLine;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class RequestLineTest {

    @Test
    void from() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);
        RequestLine requestLine = RequestLine.from(bufferedReader);

        assertAll(
                () -> assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getRequestTarget().getUri()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1)
        );
    }
}
