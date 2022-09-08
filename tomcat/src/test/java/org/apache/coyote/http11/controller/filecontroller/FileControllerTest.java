package org.apache.coyote.http11.controller.filecontroller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.controller.apicontroller.RootApiController;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.junit.jupiter.api.Test;

class FileControllerTest {

    @Test
    void FileController는_file요청을_처리할_수_있다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 파일_요청_메시지("GET /index.html HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        FileController fileController = new FileController();

        // when
        boolean result = fileController.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void FileController는_file요청이_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 파일_요청_메시지("GET /index HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        FileController fileController = new FileController();

        // when
        boolean result = fileController.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void FileController는_file요청_처리_시_file의_내용_반환한다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 파일_요청_메시지("GET /index.html HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse httpResponse = new HttpResponse(outputStream);

        FileController fileController = new FileController();

        // when
        fileController.service(httpRequest, httpResponse);
        outputStream.close();

        String expectedBody = getBody("/index.html");
        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(
                        httpResponse.ok(expectedBody)
                                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ")
                );
    }

    private static String 파일_요청_메시지(String requestLine, String body) {
        return String.join("\r\n",
                requestLine,
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);
    }

    private static HttpRequest httpRequest_생성(String requestMessage) throws IOException {
        HttpRequest httpRequest;
        try (InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            httpRequest = HttpRequest.of(bufferedReader);
        }
        return httpRequest;
    }

    private String getBody(String uri) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + uri);
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
