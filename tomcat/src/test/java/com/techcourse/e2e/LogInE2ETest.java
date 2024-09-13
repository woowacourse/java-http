package com.techcourse.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class LogInE2ETest {

    @Test
    @DisplayName("GET /login 요청에 응답한다.")
    void getLogin() throws IOException {
        // given
        final String httpRequest = """
                GET /login HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                """;

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String responseHeader = """
                HTTP/1.1 200 OK
                Content-Type: text/html;charset=utf-8
                Content-Length:""";
        URL staticResourceUrl = getClass().getClassLoader().getResource("static/login.html");
        File file = new File(staticResourceUrl.getFile());
        String logInPage = new String(Files.readAllBytes(file.toPath()));

        assertThat(socket.output()).startsWith(responseHeader);
        assertThat(socket.output()).contains(logInPage);
    }

    @Test
    @DisplayName("등록된 사용자의 올바른 POST /login 요청에 응답한다.")
    void postLogin() {
        // given
        String account = "red";
        String password = "orange";
        String email = "yellow";
        InMemoryUserRepository.save(new User(account, password, email));

        final String httpRequest = """
                POST /login HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                Content-Type: application/x-www-form-urlencoded
                Content-Length: 27
                
                account=red&password=orange
                """;

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String responseHeader = """
                HTTP/1.1 302 Found
                Location: /index.html""";

        assertThat(socket.output()).startsWith(responseHeader);
    }

    @Test
    @DisplayName("등록되지 않은 사용자의 POST /login 요청에 응답한다.")
    void postLoginFailed() {
        // given
        final String httpRequest = """
                POST /login HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                Content-Type: application/x-www-form-urlencoded
                Content-Length: 27
                
                account=rad&password=aronge
                """;

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String responseHeader = """
                HTTP/1.1 302 Found
                Location: /401.html""";

        assertThat(socket.output()).startsWith(responseHeader);
    }
}
