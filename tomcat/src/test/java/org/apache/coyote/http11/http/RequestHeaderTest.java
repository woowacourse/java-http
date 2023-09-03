package org.apache.coyote.http11.http;

import org.apache.coyote.http11.handler.RequestParser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeaderTest {

    @Test
    void header에_contectLength가_없으면_0_반환() throws IOException {
        final String httpRequest = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        RequestHeader header = RequestHeader.from(input);

        assertThat(header.getContentLength()).isEqualTo(0);
    }

    @Test
    void header에_contectLength가_있으면_해당_값_반환() throws IOException {
        int expected = 127;

        final String httpRequest = String.join("\r\n",
                "Content-Length: " + expected + " ",
                "Connection: keep-alive ",
                "",
                "");

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        RequestHeader header = RequestHeader.from(input);

        assertThat(header.getContentLength()).isEqualTo(expected);
    }

    @Test
    void cookie에_JSESSION이_없으면_false() throws IOException {
        final String httpRequest = String.join("\r\n",
                "Content-Length: 127 ",
                "Connection: keep-alive ",
                "",
                "");

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        RequestHeader header = RequestHeader.from(input);

        assertThat(header.doesNotHasJsessionCookie()).isTrue();
    }

    @Test
    void cookie에_JSESSION이_있으면_true() throws IOException {
        final String httpRequest = String.join("\r\n",
                "Content-Length: 127 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        RequestHeader header = RequestHeader.from(input);

        assertThat(header.doesNotHasJsessionCookie()).isFalse();
    }

}
