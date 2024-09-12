package org.apache.coyote.http11.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RequestFactoryTest {
    private static final String HTTP_LINE_SEPARATOR = "\r\n";
    private static final int REQUEST_CONTENT_LENGTH = 30;

    @DisplayName("HttpRequest를 RequestLine, RequestHeader, RequestBody 순서대로 읽어들인다.")
    @Test
    void testReadRequestLine() throws IOException {
        // given
        final String httpRequest = String.join(HTTP_LINE_SEPARATOR,
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader reader = new BufferedReader(inputStreamReader);

        // when
        final String actualRequestLine = RequestFactory.readRequestLine(reader);
        final String actualRequestHeaders = RequestFactory.readRequestHeaders(reader);
        final String actualRequestBody = RequestFactory.readRequestBody(reader, REQUEST_CONTENT_LENGTH);

        // then
        final String expectedRequestLine = "POST /login HTTP/1.1";
        final String expectedRequestHeaders =
                "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Content-Length: 30\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Accept: */*\r\n";
        final String expectedRequestBody = "account=gugu&password=password";

        assertAll(
                () -> assertThat(actualRequestLine).isEqualTo(expectedRequestLine),
                () -> assertThat(actualRequestHeaders).isEqualTo(expectedRequestHeaders),
                () -> assertThat(actualRequestBody).isEqualTo(expectedRequestBody)
        );
    }
}
