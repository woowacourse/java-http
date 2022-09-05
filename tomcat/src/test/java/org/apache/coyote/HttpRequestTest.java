package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void Request_헤더_생성_테스트() throws IOException {
        // given
        String requestBody = "requestBody1\r\nrequestBody2";
        int contentLength = requestBody.getBytes().length;
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + contentLength,
                "Connection: keep-alive ",
                "",
                "requestBody1",
                "requestBody2");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));

        // when
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getUrl()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getQueryParams().exists()).isFalse(),
                () -> assertThat(httpRequest.getHeaders()).hasSize(2),
                () -> assertThat(httpRequest.getBody()).isEqualTo(
                        "requestBody1\r\nrequestBody2")
        );
    }
}
