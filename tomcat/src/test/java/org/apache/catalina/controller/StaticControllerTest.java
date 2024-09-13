package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticControllerTest {

    private final StaticController staticController = new StaticController();

    @DisplayName("정적 페이지에 대해 GET 요청 시 html을 반환해야 한다.")
    @Test
    void doGet() throws IOException {
        String uri = "/index.html";
        String requestString = String.join("\r\n",
                "GET " + uri + " HTTP/1.1",
                "Host: localhost",
                "Content-Length: 0",
                "Content-Type: application/x-www-form-urlencoded",
                ""
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        staticController.doGet(request, response);
        StatusLine statusLine = response.getStatusLine();

        String path = "src/main/resources/static/index.html";
        String htmlString = new String(Files.readAllBytes(Paths.get(path)));

        assertThat(statusLine.getStatusCode()).isEqualTo(StatusCode.valueOf(200));
        assertThat(response.getResponseBody().getBody()).isEqualTo(htmlString);
    }
}
