package nextstep.jwp.model;

import nextstep.jwp.HttpServlet;
import nextstep.jwp.MockSocket;
import nextstep.jwp.model.httpmessage.session.HttpSession;
import nextstep.jwp.model.httpmessage.session.HttpSessions;
import nextstep.jwp.util.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.request.HttpMethod.GET;
import static nextstep.jwp.model.httpmessage.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    public static final String CONTENT_LENGTH = "Content-Length: ";

    @DisplayName("인덱스 페이지 조회에 대한 응답을 확인한다.")
    @Test
    void responseForward() throws IOException {
        // given
        MockSocket socket = new MockSocket("GET /index HTTP/1.1 \r\n");
        HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String output = socket.output();
        String[] outputs = output.split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(outputs[1]).isEqualTo("Content-Type: text/html;charset=utf-8 ");
        assertThat(outputs[2]).contains(CONTENT_LENGTH);
        assertThat(outputs[2].substring(CONTENT_LENGTH.length())).isNotBlank();
        assertThat(outputs[3]).isBlank();
        int bodyStartIndex = output.indexOf(outputs[4]);
        assertThat(output.substring(bodyStartIndex)).isEqualTo(FileUtils.readFileOfUrl("/index.html"));
    }

    @DisplayName("로그인 상태로 login 페이지를 조회 시 index 페이지로 리다이렉트한다.")
    @Test
    void request_GET_LOGIN_with_cookie() {
        // given
        HttpSession session = new HttpSession("1234");
        session.setAttribute("User", new User("pomo", "password", "email@email.com"));
        HttpSessions.addSession(session);

        String value = String.join("\r\n",
                GET + " /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=1234 ",
                "");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String[] outputs = socket.output().split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 302 Redirect ");
        assertThat(outputs[1]).isEqualTo("Location: /index.html ");
    }

    @DisplayName("비로그인 상태로 login 페이지를 조회 시 login 페이지를 조회한다.")
    @Test
    void request_GET_LOGIN_without_cookie() throws IOException {
        // given
        String value = String.join("\r\n",
                GET + " /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String output = socket.output();
        String[] outputs = output.split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(outputs[1]).isEqualTo("Content-Type: text/html;charset=utf-8 ");
        assertThat(outputs[2]).contains(CONTENT_LENGTH);
        assertThat(outputs[2].substring(CONTENT_LENGTH.length())).isNotBlank();
        assertThat(outputs[3]).isEmpty();
        int bodyStartIndex = output.indexOf(outputs[4]);
        assertThat(output.substring(bodyStartIndex)).isEqualTo(FileUtils.readFileOfUrl("/login.html"));
    }

    @DisplayName("401 페이지 리다이렉트에 대한 응답을 확인한다.")
    @Test
    void responseRedirect() {
        // given
        String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 32",
                "",
                "account=gugu&password=password11");
        MockSocket socket = new MockSocket(request);
        HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String expectedHeader = String.join("\r\n",
                "HTTP/1.1 302 Redirect ",
                "Location: /401.html ",
                "");
        assertThat(socket.output()).hasToString(expectedHeader);
    }

    @DisplayName("form 형식의 파라미터로 로그인에 대한 응답을 확인한다.")
    @Test
    void response_GET_LOGIN_with_params() {
        String value = String.join("\r\n",
                POST + " /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=password");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        httpServlet.run();

        String[] outputs = socket.output().split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(outputs[1]).contains("Set-Cookie: JSESSIONID=");
        assertThat(outputs[2]).isEqualTo("Content-Type: text/html;charset=utf-8 ");
        assertThat(outputs[3]).contains(CONTENT_LENGTH);
        assertThat(outputs[3].substring(CONTENT_LENGTH.length())).isNotBlank();
    }

    @DisplayName("form 형식의 파라미터로 가입에 대한 응답을 확인한다.")
    @Test
    void response_POST_REGISTER_with_params() {
        String value = String.join("\r\n",
                POST + " /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 46",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=pomo&password=password&email=email@email.com");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        httpServlet.run();

        String[] outputs = socket.output().split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 302 Redirect ");
        assertThat(outputs[1]).isEqualTo("Location: /index.html ");
    }

    @DisplayName("form 형식의 파라미터로 가입 시 중복 아이디에 대한 응답을 확인한다.")
    @Test
    void response_invalid_POST_REGISTER_with_params() throws IOException {
        String value = String.join("\r\n",
                POST + " /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 46",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=password&email=email@email.com");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        httpServlet.run();
        String output = socket.output();
        String[] outputs = output.split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 401 Unauthorized ");
        assertThat(outputs[1]).contains(CONTENT_LENGTH);
        assertThat(outputs[1].substring(CONTENT_LENGTH.length())).isNotBlank();
        assertThat(outputs[2]).isBlank();
        int bodyStartIndex = output.indexOf(outputs[3]);
        assertThat(output.substring(bodyStartIndex)).isEqualTo(FileUtils.readFileOfUrl("/401.html"));
    }
}