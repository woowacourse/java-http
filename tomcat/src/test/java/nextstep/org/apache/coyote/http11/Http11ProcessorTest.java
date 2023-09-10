package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.HandlerMapping;
import nextstep.jwp.JwpHttpDispatcher;
import nextstep.jwp.SessionManager;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootGetController;
import nextstep.jwp.interceptor.AuthInterceptor;
import nextstep.jwp.interceptor.HandlerInterceptor;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.junit.jupiter.api.Test;
import support.StubSocket;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

class Http11ProcessorTest {

    private static final Map<String, Controller> controllers =
            Map.of("/", new RootGetController(),
                    "/login", new LoginController(new SessionManager()),
                    "/register", new RegisterController(new SessionManager()));

    private static final List<HandlerInterceptor> handlerInterceptors =
            List.of(new AuthInterceptor(List.of("/login"), new SessionManager()));

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final JwpHttpDispatcher httpDispatcher = new JwpHttpDispatcher(new HandlerMapping(controllers, handlerInterceptors));
        final var processor = new Http11Processor(socket, new HttpRequestParser(), httpDispatcher);

        // when
        processor.process(socket);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

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
        final JwpHttpDispatcher httpDispatcher = new JwpHttpDispatcher(new HandlerMapping(controllers, handlerInterceptors));
        final var processor = new Http11Processor(socket, new HttpRequestParser(), httpDispatcher);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
