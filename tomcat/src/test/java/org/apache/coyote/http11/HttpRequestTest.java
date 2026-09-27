package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void parseGetRequest() throws IOException {
        //given
        String raw = "GET /login HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "\r\n";

        //when
        HttpRequest request = HttpRequest.parse(new BufferedReader(new StringReader(raw)));

        //then
        assertThat(request.requestLine()).isEqualTo(new RequestLine("GET", "/login", "", "HTTP/1.1"));
        assertThat(request.headers()).containsEntry("host", "localhost:8080");
        assertThat(request.body()).isEmpty();
    }

    @Test
    void parsePostBody() throws IOException {
        //given
        String body = "hihi";
        String raw = "POST /login HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Content-Length: 4\r\n"
                + "\r\n"
                + body;

        //when
        HttpRequest request = HttpRequest.parse(new BufferedReader(new StringReader(raw)));

        //then
        assertThat(request.requestLine()).isEqualTo(new RequestLine("POST", "/login", "", "HTTP/1.1"));
        assertThat(request.headers()).containsEntry("content-length", "4");
        assertThat(request.body()).isEqualTo("hihi");
    }
}
