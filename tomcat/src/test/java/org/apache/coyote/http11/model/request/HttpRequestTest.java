package org.apache.coyote.http11.model.request;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void test() {
        final String httpRequest= String.join("\r\n",
                "GET /index.html?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
        HttpRequest from = HttpRequest.from(reader);
        System.out.println(from);
    }

}
