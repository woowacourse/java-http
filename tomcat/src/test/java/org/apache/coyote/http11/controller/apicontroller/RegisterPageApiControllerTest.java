package org.apache.coyote.http11.controller.apicontroller;

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
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.Test;

class RegisterPageApiControllerTest {

    @Test
    void RegisterPageApiHandler는_register요청을_처리할_수_있다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 회원가입_페이지_요청("GET /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterPageApiController registerPageApiController = new RegisterPageApiController();

        // when
        boolean result = registerPageApiController.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void RegisterPageApiHandler는_register요청이_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 회원가입_페이지_요청("GET /regist HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterPageApiController registerPageApiController = new RegisterPageApiController();

        // when
        boolean result = registerPageApiController.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void RegisterPageApiHandler는_register요청이_GET이_아니면_처리할_수_없다() throws IOException {
        // given
        final String body = "";
        String requestMessage = 회원가입_페이지_요청("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        RegisterPageApiController registerPageApiController = new RegisterPageApiController();

        // when
        boolean result = registerPageApiController.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void RegisterPageApiHandler는_register요청이오면_register_html_파일을_반환한다() throws IOException {
        // given
        String body = "";
        String requestMessage = 회원가입_페이지_요청("GET /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse httpResponse = new HttpResponse(outputStream);



        RegisterPageApiController registerPageApiController = new RegisterPageApiController();

        // when
        registerPageApiController.service(httpRequest, httpResponse);
        outputStream.close();

        String expected = getBody("static/register.html");

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(httpResponse.ok(expected)
                        .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 "));
    }

    private static String 회원가입_페이지_요청(String requestLine, String body) {
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
        URL resource = getClass().getClassLoader().getResource(uri);
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
