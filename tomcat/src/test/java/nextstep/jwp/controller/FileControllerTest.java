package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.Test;

class FileControllerTest {

    @Test
    void FileController는_file요청이_들어오면_파일내용을_반환한다() throws Exception {
        // given
        String requestMessage = 파일_요청_메시지("GET /index.html HTTP/1.1 ", "");
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        FileController fileController = new FileController();

        // when
        fileController.service(httpRequest, httpResponse);

        // then
        String body = getBody("/index.html");

        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(HttpResponse.from(httpRequest).ok(ContentType.HTML, body));
    }

    @Test
    void FileController는_없는_file요청이_들어오면_notFound를_반환한다() throws Exception {
        // given
        String requestMessage = 파일_요청_메시지("GET /invalidFile.html HTTP/1.1 ", "");
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        FileController fileController = new FileController();

        // when
        assertThatThrownBy(() -> fileController.service(httpRequest, httpResponse))
                .isInstanceOf(InvocationTargetException.class);
    }

    private String getBody(String uri) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + uri);
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }

    private String 파일_요청_메시지(String requestLine, String body) {
        return String.join("\r\n",
                requestLine,
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);
    }

    private HttpRequest httpRequest_생성(String requestMessage) throws IOException {
        HttpRequest httpRequest;
        try (InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            httpRequest = HttpRequest.of(bufferedReader);
        }
        return httpRequest;
    }
}
