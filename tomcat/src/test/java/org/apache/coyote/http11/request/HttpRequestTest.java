package org.apache.coyote.http11.request;

import org.apache.coyote.http11.handler.RequestParser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestTest {

    @Test
    void queryString이_존재하면_파싱한다() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        HttpRequest request = HttpRequest.from(input);

        RequestData requestData = request.getRequestData();

        assertAll(
                () -> assertThat(requestData.find("account")).isEqualTo("gugu"),
                () -> assertThat(requestData.find("password")).isEqualTo("password")
        );
    }

    @Test
    void requestBody가_존재하면_파싱한다() throws IOException {
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        HttpRequest request = HttpRequest.from(input);

        RequestData requestData = request.getRequestData();

        assertAll(
                () -> assertThat(requestData.find("account")).isEqualTo("gugu"),
                () -> assertThat(requestData.find("password")).isEqualTo("password"),
                () -> assertThat(requestData.find("email")).isEqualTo("hkkang%40woowahan.com")
        );
    }
}
