package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import nextstep.jwp.ChicChocServlet;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {
    @BeforeEach
    void setUp() {
        final var requestMapping = RequestMapping.getRequestMapping();
        requestMapping.addServlet("/", new ChicChocServlet());
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ");

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void index() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "");
        assertThat(socket.output()).contains(expected);
    }
}
