package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StaticResourceControllerTest {

    private final Controller staticResourceController = StaticResourceController.getInstance();

    @DisplayName("정적 자원에 대한 GET 요청이 오면 정적 자원을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/401.html", "/css/styles.css", "/js/scripts.js"})
    void getStaticResource(String path) throws Exception {
        // given
        String httpRequestMessage = String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        staticResourceController.service(request, response);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/" + path);
        byte[] fileContent = Files.readAllBytes(new File(resource.getFile()).toPath());
        String fileExtension = path.split("\\.")[1];
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + fileExtension + ";charset=utf-8 ",
                "Content-Length: " + fileContent.length + " ",
                "",
                new String(fileContent),
                "");
        assertThat(outputStream.toString()).isEqualTo(expected);
        inputStream.close();
        outputStream.close();
    }
}
