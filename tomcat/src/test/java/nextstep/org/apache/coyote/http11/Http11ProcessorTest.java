package nextstep.org.apache.coyote.http11;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import org.apache.catalina.DispatcherServlet;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final RequestMapping requestMapper = new RequestMapping()
                .addHandler("/", new RootController())
                .addHandler("/login", new LoginController())
                .addHandler("register", new RegisterController());
        final Http11Processor processor = new Http11Processor(socket, new DispatcherServlet(requestMapper));

        // when
        processor.process(socket);

        // then
        final String actual = socket.output();
        assertAll(() -> {
            assertThat(actual).contains("HTTP/1.1 200 OK");
            assertThat(actual).contains("Content-Type: text/html;charset=utf-8");
        });
    }

    @Test
    void index() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapper = new RequestMapping()
                .addHandler("/", new RootController())
                .addHandler("/login", new LoginController())
                .addHandler("register", new RegisterController());
        final Http11Processor processor = new Http11Processor(socket, new DispatcherServlet(requestMapper));

        // when
        processor.process(socket);

        // then

        String actual = socket.output();
        assertAll(() -> {
            assertThat(actual).contains("HTTP/1.1 200 OK");
            assertThat(actual).contains("Content-Type: text/html;charset=utf-8");
        });
    }
}
