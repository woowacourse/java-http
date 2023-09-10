package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.Controller;
import org.apache.coyote.FileReader;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileControllerTest {

    private final Controller controller = new FileController();
    private final FileReader fileReader = new FileReader();

    @Test
    @DisplayName("특정 파일에 GET 요청을 하면 파일이 조회된다.")
    void doGet() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        HttpRequest httpRequest;
        final HttpResponse httpResponse = HttpResponse.create();
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // when
        controller.service(httpRequest, httpResponse);

        // then
        final String message = httpResponse.convertToMessage();
        final String fileContent = fileReader.readStaticFile("/css/styles.css");
        assertThat(message).contains(
            "HTTP/1.1", "200 OK",
            "Content-Type: text/css;charset=utf-8",
            "Content-Length: " + fileContent.getBytes().length
        );
    }

    @Test
    @DisplayName("FileController 에 POST 요청을 보내면 예외가 발생한다.")
    void doPost() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
            "POST /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        HttpRequest httpRequest;
        final HttpResponse httpResponse = HttpResponse.create();
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // when, then
        assertThatThrownBy(() -> controller.service(httpRequest, httpResponse))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
