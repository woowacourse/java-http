package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestHttpResponseHeadersTest {
    @Test
    void request_헤더_생성_테스트() throws IOException {
        final String request = String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBytes());
        Reader inputStreamReader = new InputStreamReader(byteArrayInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.of(bufferedReader);

        assertThat(httpRequestHeaders.getValue("Content-Length")).isEqualTo("80");
    }
}
