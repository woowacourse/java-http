package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class LoginControllerTest {

    @Test
    void page() {
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Controller controller = new LoginController();
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = controller.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Content-Type"))
                        .contains(ContentType.TEXT_HTML.getValue()),
                () -> assertThat(httpResponse.getMessageBody().getValue())
                        .isEqualTo(new String(Files.readAllBytes(new File(
                                getClass().getClassLoader().getResource("static/login.html").getFile()).toPath())))
        );
    }

    @Test
    void login() {
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Controller controller = new LoginController();
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = controller.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Location"))
                        .isEqualTo("/index.html")
        );
    }

    @Test
    void wrongPassword() {
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=wroing HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Controller controller = new LoginController();
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = controller.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Location"))
                        .isEqualTo("/401.html")
        );
    }

    @Test
    void wrongUserId() {
        String httpRequest = String.join("\r\n",
                "GET /login?account=fufu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Controller controller = new LoginController();
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = controller.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Location"))
                        .isEqualTo("/401.html")
        );
    }
}
