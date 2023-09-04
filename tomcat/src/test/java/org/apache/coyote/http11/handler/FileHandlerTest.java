package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResponseEntity;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FileHandlerTest {

    private final ClassLoader classLoader = getClass().getClassLoader();
    private final FileHandler fileHandler = new FileHandler();

    @Test
    void indexHTML파일을_반환한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        String fileData = extractFileData("/index.html");
        BufferedReader request = RequestParser.requestToInput(httpRequest);

        // when
        ResponseEntity responseEntity = fileHandler.handle(HttpRequest.from(request));
        String response = responseEntity.generateResponseMessage();

        String[] responseElements = response.split("\r\n");
        String responseBody = responseElements[responseElements.length - 1];

        // then
        assertThat(responseBody).isEqualTo(fileData);
        assertAll(
                () -> assertThat(responseBody).isEqualTo(fileData),
                () -> assertThat(response).contains("Content-Type: text/html;charset=utf-8")
        );
    }

    @Test
    void stylecss파일을_반환한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        String fileData = extractFileData("/css/styles.css");
        BufferedReader request = RequestParser.requestToInput(httpRequest);

        // when
        ResponseEntity responseEntity = fileHandler.handle(HttpRequest.from(request));
        String response = responseEntity.generateResponseMessage();

        String[] responseElements = response.split("\r\n");
        String responseBody = responseElements[responseElements.length - 1];

        // then
        assertAll(
                () -> assertThat(responseBody).isEqualTo(fileData),
                () -> assertThat(response).contains("Content-Type: text/css ")
        );
    }

    private String extractFileData(String filePath) throws IOException {
        URL resource = classLoader.getResource("static" + filePath);
        File file = new File(resource.getFile());
        String fileData = new String(Files.readAllBytes(file.toPath()));
        return fileData;
    }
}
