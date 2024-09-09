package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestReaderTest {

    @DisplayName("Http GET Request를 읽어올 수 있다.")
    @Test
    void readGetHttpRequest() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost",
                "Connection: keep-alive",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        // when
        RequestReader requestReader = new RequestReader(inputStream);
        RequestLine requestLine = requestReader.getRequestLine();
        RequestHeaders requestHeaders = requestReader.getRequestHeaders();

        // then
        assertAll(
                () -> assertThat(requestLine.getUrl()).isEqualTo("/index.html"),
                () -> assertThat(requestHeaders.getHeaderValue("Connection")).isEqualTo("keep-alive")
        );

        inputStream.close();
    }

    @DisplayName("Http POST Request를 읽어올 수 있다.")
    @Test
    void readPostHttpRequest() throws IOException {
        // given
        String expected = "account=poke&email=poke@zzang.com&password=password";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost",
                "Connection: keep-alive",
                "Content-Length: " + expected.getBytes().length,
                "",
                expected);
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        // when
        RequestReader requestReader = new RequestReader(inputStream);
        String actual = requestReader.getRequestBody();

        // then
        assertThat(actual).isEqualTo(expected);
        inputStream.close();
    }
}