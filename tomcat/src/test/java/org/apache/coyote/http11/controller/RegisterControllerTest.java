package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class RegisterControllerTest {

    private static final RegisterController CONTROLLER = new RegisterController();

    @Test
    void page() {
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = CONTROLLER.service(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Content-Type"))
                        .contains(ContentType.TEXT_HTML.getValue()),
                () -> assertThat(httpResponse.getMessageBody().getValue())
                        .isEqualTo(new String(Files.readAllBytes(new File(
                                getClass().getClassLoader().getResource("static/register.html").getFile()).toPath())))
        );
    }

    @Test
    void register() {
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 46",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=id&email=email%40email.com&password=pw");

        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = CONTROLLER.service(HttpRequest.from(bufferedReader));
        Optional<User> registeredUser = InMemoryUserRepository.findByAccount("id");
        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine())
                        .isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Location"))
                        .isEqualTo("/index.html"),
                () -> assertThat(registeredUser).isNotEmpty()
        );
    }

    @Test
    void registerFail() {
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        assertThatThrownBy(() -> CONTROLLER.service(HttpRequest.from(bufferedReader)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Account already exists");
    }
}
