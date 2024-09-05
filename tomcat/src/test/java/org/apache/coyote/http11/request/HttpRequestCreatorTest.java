package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestCreatorTest {

    @Test
    void createHttpRequestTest() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));
        RequestStartLine startLine = new RequestStartLine("GET", "/", "HTTP/1.1");
        HttpRequest expected = new HttpRequest(startLine);

        HttpRequest actual = HttpRequestCreator.createHttpRequest(reader);

        assertThat(actual).isEqualTo(expected);
    }
}
