package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.StubSocket;

class Step2Test {

    @Test
    void 로그인에_성공하면_302_상태_코드와_index_html_Location으로_응답한다() {
        // given
        String body = "account=gugu&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @ParameterizedTest
    @CsvSource({
            "unknown, password",
            "gugu, wrong"
    })
    void 로그인에_실패하면_302_상태_코드와_401_html_Location으로_응답한다(
            String account,
            String password
    ) {
        // given
        String body = "account=" + account + "&password=" + password;
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /401.html");
    }

    @Test
    void GET_쿼리_문자열로는_로그인하지_않는다() {
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200");
        assertThat(socket.output()).doesNotContain("Location:");
    }

    @Test
    void UTF_8_본문을_Content_Length의_바이트_수만큼_읽는다() {
        String body = "account=gugu&password=틀림";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /401.html");
    }

    @Test
    void 회원가입_하면_index_html로_리다이렉트한다() {
        // given
        String body = "account=whale&email=whale%40gmail.com&password=whale1234";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @Test
    void 회원가입한_계정과_비밀번호로_로그인할_수_있다() {
        String account = "whale-" + UUID.randomUUID();
        String registerBody = "account=" + account + "&email=whale%40gmail.com&password=whale1234";
        String registerRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: " + registerBody.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                registerBody);
        var registerSocket = new StubSocket(registerRequest);
        new Http11Processor(registerSocket).process(registerSocket);

        String loginBody = "account=" + account + "&password=whale1234";
        String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + loginBody.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                loginBody);
        var loginSocket = new StubSocket(loginRequest);

        new Http11Processor(loginSocket).process(loginSocket);

        assertThat(loginSocket.output()).startsWith("HTTP/1.1 302");
        assertThat(loginSocket.output()).contains("Location: /index.html");
    }

    @Test
    void 로그인_페이지에서_로그인_버튼을_누르면_POST_요청을_보낸다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    void JSESSIONID가_없는_요청에는_빈_세션을_만들고_ID를_쿠키로_보낸다() {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry",
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        String sessionId = sessionIdFrom(socket);
        assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);
        var session = new SessionManager().findSession(sessionId).orElseThrow();
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void 유효한_JSESSIONID가_있는_요청에는_새_쿠키를_설정하지_않는다() {
        String sessionId = UUID.randomUUID().toString();
        new SessionManager().add(new Session(sessionId));
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; JSESSIONID=" + sessionId,
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID=");
    }

    @Test
    void 로그인에_성공하면_응답_쿠키가_가리키는_세션에_사용자를_저장한다() {
        String body = "account=gugu&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String sessionId = sessionIdFrom(socket);
        var session = new SessionManager().findSession(sessionId).orElseThrow();

        User user = (User) session.getAttribute("user");
        assertThat(user.getAccount()).isEqualTo("gugu");
    }

    @Test
    void 로그인한_사용자가_로그인_페이지에_접근하면_홈으로_리다이렉트한다() {
        String pageRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "",
                "");
        var pageSocket = new StubSocket(pageRequest);
        new Http11Processor(pageSocket).process(pageSocket);
        String pageSessionId = sessionIdFrom(pageSocket);

        String body = "account=gugu&password=password";
        String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Cookie: JSESSIONID=" + pageSessionId,
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);
        var loginSocket = new StubSocket(loginRequest);
        new Http11Processor(loginSocket).process(loginSocket);
        String sessionId = sessionIdFrom(loginSocket);
        assertThat(sessionId).isEqualTo(pageSessionId);

        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @Test
    void 쿠키에_해당하는_세션이_없으면_로그인_페이지를_보여준다() {
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + UUID.randomUUID(),
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200");
        assertThat(socket.output()).contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    void 세션에_사용자가_없으면_로그인_페이지를_보여준다() {
        String sessionId = UUID.randomUUID().toString();
        new SessionManager().add(new Session(sessionId));
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200");
        assertThat(socket.output()).contains("<form method=\"post\" action=\"login\">");
        assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID=");
    }

    @Test
    void 쿠키에_해당하는_세션이_없으면_새_JSESSIONID를_발급한다() {
        String unknownId = UUID.randomUUID().toString();
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + unknownId,
                "",
                "");
        var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        String newSessionId = sessionIdFrom(socket);
        assertThat(newSessionId).isNotEqualTo(unknownId);
        assertThat(new SessionManager().findSession(newSessionId)).isPresent();
    }

    private String sessionIdFrom(StubSocket socket) {
        String cookieHeader = socket.output().lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .findFirst()
                .orElseThrow();
        return cookieHeader.substring("Set-Cookie: JSESSIONID=".length());
    }
}
