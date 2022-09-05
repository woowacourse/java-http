package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.util.Map;
import org.apache.coyote.http11.http.domain.HttpMethod;
import org.apache.coyote.http11.http.domain.HttpVersion;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class HttpRequestTest {

    @Test
    void from() {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);
        HttpRequest actual = HttpRequest.from(bufferedReader);

        assertAll(
                () -> assertThat(actual.getRequestLine().getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(actual.getRequestLine().getRequestTarget().getUri()).isEqualTo("/index.html"),
                () -> assertThat(actual.getRequestLine().getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1),
                () -> assertThat(actual.getHeaders().getValue()).containsAllEntriesOf(Map.of(
                        "Host", "localhost:8080",
                        "Connection", "keep-alive")),
                () -> assertThat(actual.getMessageBody().getValue()).isEqualTo("")
        );
    }
}
