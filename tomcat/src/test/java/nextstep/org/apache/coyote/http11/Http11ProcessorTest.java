package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.UserNotFoundException;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
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
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
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
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css_파일_요청_시_응답_헤더_Content_Type에_css가_포함된다() throws URISyntaxException, IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.run();

        // then
        URI uri = getClass().getClassLoader().getResource("static/css/styles.css").toURI();
        byte[] styles = Files.readAllBytes(Paths.get(uri));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + styles.length + " ",
                "",
                new String(styles));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login으로_요청이_올_경우_login_html을_반환한다() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.run();

        // then
        final URI uri = getClass().getClassLoader().getResource("static/login.html").toURI();
        final byte[] login = Files.readAllBytes(Paths.get(uri));
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + login.length + " ",
                "",
                new String(login));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void query_parameter로_들어온_계정_정보가_일치할_경우_print한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // when
        processor.run();

        // then
        String expected = InMemoryUserRepository.findByAccount("gugu").orElseThrow().toString().concat("\n");
        assertThat(outContent.toString()).isEqualTo(expected);
    }

    @Test
    void query_parameter로_들어온_계정_정보가_없을_경우_예외를_반환한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=eden&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when & then
        assertThatThrownBy(processor::run)
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void query_parameter로_들어온_계정_정보가_일치하지_않을_경우_예외를_반환한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=gugugugu HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when & then
        assertThatThrownBy(processor::run)
                .isInstanceOf(AuthenticationException.class);
    }
}
