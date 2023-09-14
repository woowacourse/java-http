package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import nextstep.jwp.db.InMemorySessionRepository;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String responseBody = "Hello world!";
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @ParameterizedTest(name = "요청이 {0} 일 때 {1} 를 body 로 반환한다.")
    @CsvSource(value = {
        "GET /index.html:static/index.html:text/html;charset=utf-8",
        "GET /login:static/login.html:text/html;charset=utf-8",
        "GET /register:static/register.html:text/html;charset=utf-8",
        "GET /css/styles.css:static/css/styles.css:text/css",
        "GET /js/scripts.js:static/js/scripts.js:text/javascript"
    }, delimiter = ':')
    void view_test(final String startLine, final String resourcePath, final String contentType)
        throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            startLine + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: " + contentType + " ",
            "Content-Length: " + responseBody.length + " ",
            "",
            new String(responseBody));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_success() {
        // given
        final String formData = "account=gugu&password=password";

        final String httpRequest = String.join(System.lineSeparator(),
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + formData.getBytes().length + " ",
            "",
            formData);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final User expectUser = InMemoryUserRepository.findByAccount("gugu").get();
        final Session expectSession = InMemorySessionRepository.findByUser(expectUser).get();

        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 302 Found ",
            "Location: /index.html ",
            "Set-cookie: JSESSIONID=" + expectSession.getUuid() + " ",
            "",
            ""
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_fail() throws IOException {
        // given
        final String formData = "account=gug&password=password";

        final String httpRequest = String.join(System.lineSeparator(),
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + formData.getBytes().length + " ",
            "",
            formData);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.length + " ",
            "",
            new String(responseBody));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_session_exist() throws IOException {
        final User existUser = InMemoryUserRepository.findByAccount("gugu").get();
        final Session userSession = InMemorySessionRepository.findByUser(existUser).get();

        final String httpRequest = String.join(System.lineSeparator(),
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Cookie: JSESSIONID=" + userSession.getUuid() + " ",
            "",
            ""
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.length + " ",
            "",
            new String(responseBody));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_post() {
        // given
        final String formData = "account=split&email=split@daum.net&password=1234";

        final String httpRequest = String.join(System.lineSeparator(),
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + formData.getBytes().length + " ",
            "",
            formData);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 302 Found ",
            "Location: /index.html ",
            "",
            ""
        );

        final Optional<User> createdUser = InMemoryUserRepository.findByAccount("split");

        Assertions.assertAll(
            () -> assertThat(createdUser).isPresent(),
            () -> assertThat(createdUser.get().checkPassword("1234")).isTrue(),
            () -> assertThat(socket.output()).isEqualTo(expected)
        );
    }
}
