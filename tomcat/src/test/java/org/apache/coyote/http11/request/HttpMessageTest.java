package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HttpMessageTest {

    @Test
    @DisplayName("read Http request")
    void readHttpRequest() {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 39",
            "Content-Type: application/x-www-form-urlencoded",
            "",
            "name=sojukang&email=kangsburg@gmail.com");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        // when
        final HttpMessage message = new HttpMessage(inputStream);

        // then
        String expectedRequestLine = "GET /index.html HTTP/1.1 ";
        String expectedHeaders = String.join("\r\n",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 39",
            "Content-Type: application/x-www-form-urlencoded");
        String expectedMessageBody = "name=sojukang&email=kangsburg@gmail.com";

        Assertions.assertAll(
            () -> assertThat(message.getRequestLine()).isEqualTo(expectedRequestLine),
            () -> assertThat(message.getHeaders()).isEqualTo(expectedHeaders),
            () -> assertThat(message.getMessageBody()).isEqualTo(expectedMessageBody)
        );
    }
}