package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new RequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK ")
                .doesNotContain("Set-Cookie")
                .contains("Content-Type: text/html;charset=utf-8 ")
                .contains("Content-Length: 12 ")
                .endsWith("\r\n\r\nHello world!");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new RequestMapping());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK ")
                .doesNotContain("Set-Cookie")
                .contains("Content-Type: text/html;charset=utf-8 ")
                .contains("Content-Length: 5564 ")
                .endsWith("\r\n\r\n" + body);
    }

    @Test
    void 요청에_JSESSIONID가_있으면_SetCookie를_반환하지_않는다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new RequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK ")
                .doesNotContain("Set-Cookie");
    }

    @Test
    void 로그인에_성공하면_세션_아이디를_SetCookie로_반환한다() {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new RequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /index.html ")
                .contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void 로그인한_상태로_로그인_페이지에_접근하면_인덱스로_리다이렉트한다() {
        // given
        final String sessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        final Session session = new Session(sessionId);
        session.setAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));
        SessionManager.getInstance().add(session);

        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new RequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /index.html ");
    }

    @Test
    void 로그인하지_않으면_로그인_페이지를_보여준다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new RequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK ");
    }
}
