package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.exception.ExceptionHandler;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("GET / 요청은 index.html 파일을 응답한다")
    @Test
    void process() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, toRequestMapping(), new SessionManager());

        // when
        processor.process(socket);

        // then
        final var actual = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assertThat(actual).startsWith("HTTP/1.1 200 OK \r\n");
        assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n");
        assertThat(actual).contains("Content-Length: 5564 \r\n");
        assertThat(actual).endsWith(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    @DisplayName("GET /index.html 요청은 index.html 파일을 응답한다")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, toRequestMapping(), new SessionManager());

        // when
        processor.process(socket);

        // then
        final var actual = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assertThat(actual).startsWith("HTTP/1.1 200 OK \r\n");
        assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n");
        assertThat(actual).contains("Content-Length: 5564 \r\n");
        assertThat(actual).endsWith(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    private RequestMapping toRequestMapping() {
        final var exceptionHandler = new ExceptionHandler();
        return RequestMapping.of(List.of(new HomeController(exceptionHandler),
                new ResourceController(exceptionHandler)));
    }
}
