package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;
import support.HttpMessageFactory;

class LoginControllerTest {

    private static final LoginController CONTROLLER = new LoginController();

    @Test
    void get() {
        String httpRequest = HttpMessageFactory.get("/login");

        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = CONTROLLER.service(HttpRequest.from(bufferedReader));

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
    void post() {
        String httpRequest = HttpMessageFactory.post("/login", "account=gugu&password=password");

        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = CONTROLLER.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Location"))
                        .isEqualTo("/index.html"),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Set-Cookie"))
                        .startsWith("JSESSIONID")
        );
    }

    @Test
    void getAfterLogin() {
        User gugu = InMemoryUserRepository.findByAccount("gugu")
                .orElseThrow();
        Session session = new Session("656cef62-e3c4-40bc-a8df-94732920ed46");
        session.setAttribute("user", gugu);
        SessionManager.add(session);
        String httpRequest = HttpMessageFactory.getWithCookie("/login",
                "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = CONTROLLER.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Location"))
                        .isEqualTo("/index.html")
        );
    }

    @Test
    void getWithInvalidJsessionid() {
        User gugu = InMemoryUserRepository.findByAccount("gugu")
                .orElseThrow();
        Session session = new Session("something-different-jsessionid");
        session.setAttribute("user", gugu);
        SessionManager.add(session);
        String httpRequest = HttpMessageFactory.getWithCookie("/login",
                "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = CONTROLLER.service(HttpRequest.from(bufferedReader));

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
    void wrongPassword() {
        String httpRequest = HttpMessageFactory.post("/login", "account=gugu&password=wrong");

        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = CONTROLLER.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Location"))
                        .isEqualTo("/401.html")
        );
    }

    @Test
    void wrongUserId() {
        String httpRequest = HttpMessageFactory.post("/login", "account=fufu&password=password");

        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = CONTROLLER.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Location"))
                        .isEqualTo("/401.html")
        );
    }
}
