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

class RegisterControllerTest {


    //    @Test
//    void registerController는_회원가입을_진행할_수_있다() throws Exception {
//        // given
//        String body = "account=gugo&password=password&email=hkkang@woowahan.com";
//        String requestMessage = 요청_메시지("POST /register HTTP/1.1 ", body);
//        HttpRequest httpRequest = httpRequest_생성(requestMessage);
//        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        final HttpResponse httpResponse = HttpResponse.of(outputStream, httpRequest);
//
//        RegisterController registerController = new RegisterController();
//
//        // when
//        registerController.service(httpRequest, httpResponse);
//
//        // then
//        assertThat(httpResponse).usingRecursiveComparison()
//                .isEqualTo(HttpResponse.of(outputStream, httpRequest).found("/index.html "));
//
//        outputStream.close();
//    }
    @Test
    void registerController는_회원가입에_실패하면_예외를_던진다() throws Exception {
        // given
        String body = "account=gugu&password=&email=hkkang@woowahan.com";
        String requestMessage = 요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        RegisterController registerController = new RegisterController();

        // when
        assertThatThrownBy(() -> registerController.service(httpRequest, httpResponse))
                .isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void RegisterController는_GET요청이오면_register_html_파일을_반환한다() throws Exception {
        // given
        String body = "";
        String requestMessage = 요청_메시지("GET /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        RegisterController registerController = new RegisterController();

        // when
        registerController.service(httpRequest, httpResponse);

        String expected = getBody("static/register.html");

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(httpResponse.ok(ContentType.HTML, expected));
    }

    private String 요청_메시지(String requestLine, String body) {
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

    private String getBody(String uri) throws IOException {
        URL resource = getClass().getClassLoader().getResource(uri);
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
