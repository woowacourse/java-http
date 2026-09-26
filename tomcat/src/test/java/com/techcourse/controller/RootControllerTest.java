package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class RootControllerTest {

    @Test
    void 루트_경로의_응답을_작성한다() throws Exception {
        Controller controller = new RootController();
        HttpRequest request = HttpRequest.readFrom(
                new ByteArrayInputStream(
                        "GET / HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8)
                )
        );
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual).startsWith("HTTP/1.1 200 OK");
        assertThat(actual).contains("Content-Type: text/html");
        assertThat(actual).endsWith("Hello world!");
    }
}
