package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class ResourceControllerTest {

    @Test
    void handle() {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Controller controller = new ResourceController();
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = controller.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Content-Type")).contains(
                        ContentType.TEXT_HTML.getValue()),
                () -> assertThat(httpResponse.getMessageBody().getValue()).isEqualTo(new String(
                        Files.readAllBytes(new File(
                                getClass().getClassLoader().getResource("static/index.html").getFile()).toPath())))
        );
    }
}
