package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

class LoginControllerTest {

//    @Test
//    void LoginController는_로그인_처리_성공_시_302_index_html을_반환한다() throws Exception {
//        // given
//        final String body = "account=gugu&password=password";
//        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
//        final HttpRequest httpRequest = httpRequest_생성(requestMessage);
//
//        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        final HttpResponse httpResponse = HttpResponse.of(outputStream, httpRequest);
//
//        LoginController loginController = new LoginController();
//
//        // when
//        loginController.service(httpRequest, httpResponse);
//
//        // then
//        assertThat(httpResponse).usingRecursiveComparison()
//                .isEqualTo(HttpResponse.of(outputStream, httpRequest)
//                        .found("/index.html "));
//
//        outputStream.close();
//    }

    @Test
    void LoginController는_존재하지않는_account가_요청되면_예외를_던진다() throws Exception {
        // given
        final String body = "account=gogo&password=password";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final HttpResponse httpResponse = HttpResponse.from(httpRequest.getHttpVersion());

        LoginController loginController = new LoginController();

        // when & then
        assertThatThrownBy(() -> loginController.service(httpRequest, httpResponse))
                .isInstanceOf(InvocationTargetException.class);

        outputStream.close();
    }

    @Test
    void LoginController는_password가_일치하지않으면_예외를_던진다() throws Exception {
        // given
        final String body = "account=gogo&password=invalidpassword";
        final String requestMessage = 로그인_요청_메시지("POST /login HTTP/1.1 ", body);
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest.getHttpVersion());

        LoginController loginController = new LoginController();

        // when & then
        assertThatThrownBy(() -> loginController.service(httpRequest, httpResponse))
                .isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void LoginController는_GET요청이_들어오면_로그인_페이지를_반환한다() throws Exception {
        // given
        final String requestMessage = 로그인_요청_메시지("GET /login HTTP/1.1 ", "");
        final HttpRequest httpRequest = httpRequest_생성(requestMessage);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest.getHttpVersion());

        LoginController loginController = new LoginController();

        // when
        loginController.service(httpRequest, httpResponse);

        String body = getBody("/login.html");

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(HttpResponse.from(httpRequest.getHttpVersion()).ok(ContentType.HTML, body));
    }

    private String 로그인_요청_메시지(String requestLine, String body) {
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
        URL resource = getClass().getClassLoader().getResource("static" + uri);
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
