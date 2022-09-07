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
import support.HttpFactory;

class RegisterControllerTest {

    private static final Controller CONTROLLER = new RegisterController();

    @Test
    void page() throws Exception {
        String httpRequest = HttpFactory.get("/register");
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);
        HttpResponse httpResponse = HttpFactory.create();
        CONTROLLER.service(HttpRequest.from(bufferedReader), httpResponse);

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
    void register() throws Exception {
        String httpRequest = HttpFactory.post("/register", "account=id&email=email%40email.com&password=pw");
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);
        HttpResponse httpResponse = HttpFactory.create();
        CONTROLLER.service(HttpRequest.from(bufferedReader), httpResponse);

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
        String httpRequest = HttpFactory.post("/register",
                "account=gugu&password=password&email=hkkang%40woowahan.com");
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);
        HttpResponse httpResponse = HttpFactory.create();

        assertThatThrownBy(() -> CONTROLLER.service(HttpRequest.from(bufferedReader), httpResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Account already exists");
    }
}
