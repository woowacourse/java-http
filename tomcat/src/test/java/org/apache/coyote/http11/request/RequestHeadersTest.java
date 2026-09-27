package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    void getHeader_success() {
        // given
        final String headers = String.join(
                "\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        final HttpRequestInput input = createInput(headers);

        // when
        final RequestHeaders requestHeaders = new RequestHeaders(input);

        // then
        assertThat(requestHeaders.getHeader("Host"))
                .isEqualTo("localhost:8080");

        assertThat(requestHeaders.getHeader("Connection"))
                .isEqualTo("keep-alive");
    }

    @Test
    void getHeader_ignoreCase_success() {
        // given
        final String headers = String.join(
                "\r\n",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                ""
        );

        final HttpRequestInput input =
                createInput(headers);

        // when
        final RequestHeaders requestHeaders = new RequestHeaders(input);

        // then
        assertThat(requestHeaders.getHeader("content-type"))
                .isEqualTo("application/x-www-form-urlencoded");

        assertThat(requestHeaders.getHeader("CONTENT-TYPE"))
                .isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void getContentLength_success() {
        // given
        final String headers = String.join(
                "\r\n",
                "Content-Length: 30",
                "",
                ""
        );

        final HttpRequestInput input = createInput(headers);

        // when
        final RequestHeaders requestHeaders = new RequestHeaders(input);

        // then
        assertThat(requestHeaders.getContentLength())
                .isEqualTo(30);
    }

    private HttpRequestInput createInput(
            final String value
    ) {
        return new HttpRequestInput(
                new ByteArrayInputStream(
                        value.getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}
