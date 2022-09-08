package org.apache.coyote.http11.frontcontroller;

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
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.Headers;
import org.apache.coyote.http11.httpmessage.request.Http11Version;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.apache.coyote.http11.httpmessage.response.StatusLine;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @Test
    void file_요청의_httpRequest를_받으면_httpResponse_내부의_값을_세팅한다() throws IOException {
        // given
        String requestMessage = 요청_메시지("GET /index.html HTTP/1.1 ", "");
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(outputStream);

        FrontController frontController = new FrontController();

        // when
         frontController.doDispatch(httpRequest, httpResponse);

        // then
        String expectedBody = getBody("/index.html");

        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(new HttpResponse(outputStream).ok(expectedBody)
                        .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 "));

        outputStream.close();
    }

    @Test
    void api_요청의_httpRequest를_받으면_httpResponse_내부의_값을_세팅한다() throws IOException {
        // given
        String body = "";
        String requestMessage = 요청_메시지("GET / HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(outputStream);

        FrontController frontController = new FrontController();

        // when
        frontController.doDispatch(httpRequest, httpResponse);

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(new HttpResponse(outputStream).ok("Hello world!")
                                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ")
                );

        outputStream.close();
    }

    private static String 요청_메시지(String requestLine, String body) {
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
