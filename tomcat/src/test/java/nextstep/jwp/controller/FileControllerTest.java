package nextstep.jwp.controller;

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
import nextstep.jwp.controller.FileController;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.Test;

class FileControllerTest {

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

    @Test
    void FileController는_file요청이_들어오면_파일내용을_반환한다() throws Exception {
        // given
        String requestMessage = 파일_요청_메시지("GET /index.html HTTP/1.1 ", "");
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse httpResponse = HttpResponse.of(outputStream, httpRequest);

        FileController fileController = new FileController();

        // when
        fileController.service(httpRequest, httpResponse);

        // then
        String body = getBody("/index.html");

        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(HttpResponse.of(outputStream, httpRequest).ok(ContentType.HTML, body));
    }

    private String getBody(String uri) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + uri);
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
