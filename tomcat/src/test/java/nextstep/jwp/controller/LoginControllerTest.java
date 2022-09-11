package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void get메서드로_로그인을_하면_로그인_페이지로_이동한다() throws IOException, URISyntaxException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.read(bufferedReader);

        // when
        LoginController loginController = new LoginController();
        HttpResponse response = loginController.process(request);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3477 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.getHttpResponse()).isEqualTo(expected);
    }

    @Test
    void post메서드로_로그인을_성공하면_302_status_code를_반환한다() throws IOException, URISyntaxException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.read(bufferedReader);

        // when
        LoginController loginController = new LoginController();
        HttpResponse response = loginController.process(request);

        // then
        var expected = "HTTP/1.1 302 FOUND";

        assertThat(response.getHttpResponse()).startsWith(expected);
    }

    @Test
    void post메서드로_로그인을_실패하면_401_status_code를_반환한다() throws IOException, URISyntaxException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=gugu&password=1234");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.read(bufferedReader);

        // when
        LoginController loginController = new LoginController();
        HttpResponse response = loginController.process(request);

        // then
        var expected = "HTTP/1.1 401 UNAUTHORIZED";

        assertThat(response.getHttpResponse()).startsWith(expected);
    }

    @Test
    void post메서드로_첫로그인을_성공하면_응답에_setCookie가_존재한다() throws IOException, URISyntaxException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.read(bufferedReader);

        // when
        LoginController loginController = new LoginController();
        HttpResponse response = loginController.process(request);

        // then
        var expected = "Set-Cookie: JSESSIONID=";

        assertThat(response.getHttpResponse()).contains(expected);
    }

    @Test
    void get메서드로_로그인을_할때_sessionId가_있으면_index페이지로_이동한다() throws IOException, URISyntaxException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: Idea-49c883f1=b9e30000-da2e-47e2-b2b0-2fa9d20fdc25; Idea-49c883f3=97fea232-d244-4359-a126-2bc7d2829fb3; JSESSIONID=82184244-d8bf-4705-bfbd-f8d05412a8c5",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.read(bufferedReader);

        // when
        LoginController loginController = new LoginController();
        HttpResponse response = loginController.process(request);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.getHttpResponse()).contains(expected);
    }
}
