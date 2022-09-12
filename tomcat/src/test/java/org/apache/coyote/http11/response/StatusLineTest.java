package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusLineTest {

    @Test
    @DisplayName("HttpRequest와 StatusCode를 받아 StatusLine을 반환한다.")
    void generate() {
        // given
        final HttpRequest request = generateRequest();

        // when
        final StatusLine statusLine = new StatusLine(request, StatusCode.OK);
        final String actual = statusLine.generate();
        final String expected = "HTTP/1.1 200 OK ";

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private HttpRequest generateRequest() {
        final String httpRequest = String.join("\r\n",
            "GET /path HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 39",
            "Content-Type: application/x-www-form-urlencoded",
            "",
            "name=sojukang&email=kangsburg@gmail.com");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        try {
            return new HttpRequest(inputStream);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("Invalid byte requested");
        }
    }
}