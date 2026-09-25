package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
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
        final String httpRequest= String.join("\r\n",
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
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
    @Test
    void 로그인에_성공하면_index_html로_리다이렉트한다() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /index.html");
    }

    @Test
    void 로그인에_실패하면_401_html로_리다이렉트한다() {
        // given
        final String requestBody = "account=gugu&password=wrong";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /401.html");
    }

    @Test
    void 회원가입에_성공하면_회원을_저장하고_index_html로_리다이렉트한다() {
        // given
        final String requestBody = "account=rudy&email=rudy@woowa.com&password=1234";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /index.html");
        assertThat(InMemoryUserRepository.findByAccount("rudy")).isPresent();
    }

    @Test
    void 회원가입_페이지를_응답한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        assertThat(socket.output()).contains("HTTP/1.1 200 OK", new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }
    @Test
    void 로그인에_성공하면_JSESSIONID_쿠키를_전달한다() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void 로그인하지_않은_상태로_로그인_페이지에_접근하면_JSESSIONID_쿠키를_전달한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 200 OK", "Set-Cookie: JSESSIONID=");
    }

    @Test
    void 로그인한_상태로_로그인_페이지에_접근하면_index_html로_리다이렉트한다() {
        // given
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));
        SessionManager.getInstance().add(session);

        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: yummy_cookie=choco; JSESSIONID=" + session.getId() + " ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /index.html");
    }

    @Test
    void 회원가입_시_URL_인코딩된_값을_디코딩한다() {
        // given
        final String requestBody = "account=rudy2&email=rudy%2Btest%40woowa.com&password=1234";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(InMemoryUserRepository.findByAccount("rudy2"))
                .isPresent()
                .get()
                .hasToString("User{id=null, account='rudy2', email='rudy+test@woowa.com', password='1234'}");
    }

    @Test
    void 이미_존재하는_계정으로_회원가입하면_401_html로_리다이렉트하고_기존_정보를_유지한다() {
        // given
        final String requestBody = "account=gugu&email=hacker@evil.com&password=hacked";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /401.html");
        assertThat(InMemoryUserRepository.findByAccount("gugu"))
                .isPresent()
                .get()
                .hasToString("User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}");
    }

    @Test
    void 빈_값으로_회원가입하면_401_html로_리다이렉트한다() {
        // given
        final String requestBody = "account=&email=&password=";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /401.html");
    }

    @Test
    void 로그인_페이지에서_발급받은_세션이_있으면_로그인_성공_시_세션을_재사용한다() {
        // given
        final String requestBody = "account=gugu&password=password";
        final Session preIssuedSession = new Session(UUID.randomUUID().toString());
        SessionManager.getInstance().add(preIssuedSession);

        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Cookie: JSESSIONID=" + preIssuedSession.getId() + " ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /index.html");
        assertThat(socket.output()).doesNotContain("Set-Cookie");
        assertThat(preIssuedSession.getAttribute("user")).isNotNull();
    }
}
