package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    void getHeader_success() {
        // given
        final String headers = String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        final BufferedReader reader = new BufferedReader(
                new StringReader(headers)
        );

        // when
        final RequestHeaders requestHeaders = new RequestHeaders(reader);

        // then
        assertThat(requestHeaders.getHeader("Host"))
                .isEqualTo("localhost:8080");
        assertThat(requestHeaders.getHeader("Connection"))
                .isEqualTo("keep-alive");
    }

    @Test
    void getHeader_ignoreCase_success() {
        // given
        final String headers = String.join("\r\n",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                ""
        );

        final BufferedReader reader = new BufferedReader(
                new StringReader(headers)
        );

        // when
        final RequestHeaders requestHeaders = new RequestHeaders(reader);

        // then
        assertThat(requestHeaders.getHeader("content-type"))
                .isEqualTo("application/x-www-form-urlencoded");
        assertThat(requestHeaders.getHeader("CONTENT-TYPE"))
                .isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void getContentLength_success() {
        // given
        final String headers = String.join("\r\n",
                "Content-Length: 30",
                "",
                ""
        );

        final BufferedReader reader = new BufferedReader(
                new StringReader(headers)
        );

        // when
        final RequestHeaders requestHeaders = new RequestHeaders(reader);

        // then
        assertThat(requestHeaders.getContentLength())
                .isEqualTo(30);
    }
}
