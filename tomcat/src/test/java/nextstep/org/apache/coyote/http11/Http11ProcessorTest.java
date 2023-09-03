package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.HandlerResolver;
import nextstep.jwp.JwpHttpDispatcher;
import nextstep.jwp.handler.get.LoginGetHandler;
import nextstep.jwp.handler.get.RegisterGetHandler;
import nextstep.jwp.handler.get.RootGetHandler;
import nextstep.jwp.handler.post.LoginPostHandler;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.junit.jupiter.api.Test;
import support.StubSocket;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

class Http11ProcessorTest {

    private final Map<String, Handler> httpGetHandlers =
            Map.of("/", new RootGetHandler(),
                    "/login", new LoginGetHandler(),
                    "/register", new RegisterGetHandler());
    private final Map<String, Handler> httpPostHandlers =
            Map.of("/login", new LoginPostHandler());

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final JwpHttpDispatcher httpDispatcher = new JwpHttpDispatcher(new HandlerResolver(httpGetHandlers, httpPostHandlers));
        final var processor = new Http11Processor(socket, new HttpRequestParser(), httpDispatcher);

        // when
        processor.process(socket);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
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
        final JwpHttpDispatcher httpDispatcher = new JwpHttpDispatcher(new HandlerResolver(httpGetHandlers, httpPostHandlers));
        final var processor = new Http11Processor(socket, new HttpRequestParser(), httpDispatcher);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
