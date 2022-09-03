package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.InvalidLoginFomratException;
import org.apache.coyote.exception.InvalidPasswordException;
import org.apache.coyote.exception.MemberNotFoundException;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
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
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5670 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedResponseBody);
    }

    @Test
    void 확장자가_없는_URI로_접근하면_HTML_요청으로_받는다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5670 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인_페이지_렌더링() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedResponseBody);
    }

    @Test
    void query_string이_있어도_로그인_페이지를_렌더링한다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedResponseBody);
    }

    @Test
    void 로그인_페이지로_접근_시_query_string으로_회원_정보를_찾아서_로그를_남긴다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        Logger logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        logger.addAppender(listAppender);
        listAppender.start();

        // when
        processor.process(socket);
        List<ILoggingEvent> logs = listAppender.list;
        String message = logs.get(0).getMessage();

        // then
        User user = InMemoryUserRepository.findByAccount("gugu")
                .orElseThrow(MemberNotFoundException::new);

        assertThat(message).isEqualTo(user.toString());
    }

    @Test
    void 계정_정보가_올바르지_않으면_예외를_반환한다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=invalid&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when, then
        assertThatThrownBy(() -> processor.process(socket))
                .isExactlyInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 비밀번호가_올바르지_않으면_예외를_반환한다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=invalid HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when, then
        assertThatThrownBy(() -> processor.process(socket))
                .isExactlyInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void 쿼리스트링에_account나_password가_들어있지_않으면_예외를_반환한다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?query=invalid HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when, then
        assertThatThrownBy(() -> processor.process(socket))
                .isExactlyInstanceOf(InvalidLoginFomratException.class);
    }
}
