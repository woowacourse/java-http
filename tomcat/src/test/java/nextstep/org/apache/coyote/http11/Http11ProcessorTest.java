package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import nextstep.jwp.ui.FileController;
import org.apache.coyote.http11.RequestMapping;
import nextstep.jwp.ui.HomeController;
import nextstep.jwp.ui.LoginController;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.registerController("/", new HomeController());
        final var processor = new Http11Processor(socket, requestMapping);

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
                "Cookie: JSESSIONID=eden ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.setFileController(new FileController());
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

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
                "Cookie: JSESSIONID=eden ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.setFileController(new FileController());
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

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
                "Cookie: JSESSIONID=eden ",
                "Content-Length: " + "a=b&b=a@w".getBytes().length,
                "",
                "a=b&b=a@w");
        final StubSocket socket = new StubSocket(httpRequest);
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.registerController("/login", new LoginController());
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

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
}
