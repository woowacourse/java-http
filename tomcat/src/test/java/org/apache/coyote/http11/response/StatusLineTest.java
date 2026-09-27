package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class StatusLineTest {

    @Test
    void create_success() {
        // given
        final StatusLine statusLine = new StatusLine(
                "HTTP/1.1",
                200,
                "OK"
        );

        // when & then
        assertThat(statusLine.getVersion())
                .isEqualTo("HTTP/1.1");

        assertThat(statusLine.getStatusCode())
                .isEqualTo(200);

        assertThat(statusLine.getReasonPhrase())
                .isEqualTo("OK");
    }

    @Test
    void createResponseLine_success() {
        // given
        final StatusLine statusLine = new StatusLine(
                "HTTP/1.1",
                200,
                "OK"
        );

        // when
        final String result = statusLine.toResponseLine();

        // then
        assertThat(result)
                .isEqualTo("HTTP/1.1 200 OK");
    }
}
