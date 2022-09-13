package org.apache.coyote.http11.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.http.HttpRequest;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @Test
    void httpRequest_withHeader() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        //when
        InputStream is = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        HttpRequest actual = HttpRequestParser.parse(bufferedReader);

        //then
        assertThat(actual.getHeaders().get("Connection")).isNotNull();
    }

    @Test
    void httpRequest_withRequestBody() throws IOException {
        // given
        String requestBody = "account=id&password=qwer1234";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        //when
        InputStream is = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        HttpRequest actual = HttpRequestParser.parse(bufferedReader);

        //then
        assertThat(actual.getRequestBody().containsKey("account")).isTrue();
    }

    @Test
    void httpRequest_withCookie() throws IOException {
        // given
        String cookie = "JSESSIONID=sample-id";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: " + cookie,
                "",
                "");
        //when
        InputStream is = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        HttpRequest actual = HttpRequestParser.parse(bufferedReader);

        //then
        assertThat(actual.getJSessionId()).isNotNull();
    }
}
