package org.apache.coyote.http11.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.techcourse.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.InputReader;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private LoginController controller;

    @BeforeEach
    void setUp() {
        controller = new LoginController();
    }

    @Test
    @DisplayName("요청 uri에 해당하는 파일을 찾아 결과를 반환한다.")
    void process1() throws IOException {
        String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = getHttpRequest(request);

        HttpResponse httpResponse = controller.process(httpRequest);

        assertThat(httpResponse.getLocation()).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("로그인한 사용자가 요청을 보내면 기본 페이지로 리다이렉트된다.")
    void process2() throws IOException {
        Session session = addSession();
        String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Cookie: JSESSIONID=" + session.getId() + " ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = getHttpRequest(request);

        HttpResponse httpResponse = controller.process(httpRequest);

        assertThat(httpResponse.getLocation()).isEqualTo("/index.html");
    }

    private Session addSession() {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", new User("account", "password", "email"));

        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        return session;
    }

    @Test
    @DisplayName("Query String을 파싱해서 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과를 출력한다.")
    void process3() throws IOException {
        String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = getHttpRequest(request);

        Logger logger = (Logger) LoggerFactory.getLogger(LoginController.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        HttpResponse httpResponse = controller.process(httpRequest);

        assertThat(listAppender.list.get(0).getFormattedMessage()).isEqualTo("user : User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}");
        assertThat(httpResponse.getLocation()).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("로그인 성공 시 기본 페이지로 리다이렉트된다.")
    void process4() throws IOException {
        String body = "account=gugu&password=password";
        String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
        HttpRequest httpRequest = getHttpRequest(request);

        HttpResponse httpResponse = controller.process(httpRequest);

        assertThat(httpResponse.getLocation()).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("로그인 성공 시 JSESSIONID를 응답 헤더로 반환한다.")
    void process5() throws IOException {
        String body = "account=gugu&password=password";
        String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
        HttpRequest httpRequest = getHttpRequest(request);

        HttpResponse httpResponse = controller.process(httpRequest);

        assertThat(httpResponse.getResponse()).contains("JSESSIONID");
    }

    @Test
    @DisplayName("로그인 실패 시 /401.html 페이지로 리다이렉트된다.")
    void process6() throws IOException {
        String body = "account=gugu&password=1234";
        String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
        HttpRequest httpRequest = getHttpRequest(request);

        HttpResponse httpResponse = controller.process(httpRequest);

        assertThat(httpResponse.getLocation()).isEqualTo("/401.html");
    }

    private HttpRequest getHttpRequest(String request) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        InputReader inputReader = new InputReader(inputStream);

        return new HttpRequest(inputReader);
    }
}
