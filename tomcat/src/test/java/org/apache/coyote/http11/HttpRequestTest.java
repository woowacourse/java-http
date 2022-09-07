package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void create() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "eee");

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        HttpRequest request = HttpRequest.from(bufferedReader);

        System.out.println(request);
    }
}