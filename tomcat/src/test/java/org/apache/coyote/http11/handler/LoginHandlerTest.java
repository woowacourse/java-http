package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LoginHandlerTest {

    private final LoginHandler loginHandler = new LoginHandler();

    @Test
    void 로그인에_성공하면_indexhtml_로_리다이렉션() throws IOException {
        String validAccount = "gugu";
        String validPassword = "password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=" + validAccount + "&password=" + validPassword);

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        ResponseEntity responseEntity = loginHandler.handle(HttpRequest.from(input));
        String response = responseEntity.generateResponseMessage();

        assertThat(response).contains(
                "Location: index.html",
                "HTTP/1.1 302 "
        );
    }

    @Test
    void 로그인에_성공하면_세션이_추가된다() throws IOException {
        // given
        String validAccount = "gugu";
        String validPassword = "password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=" + validAccount + "&password=" + validPassword);

        BufferedReader input = RequestParser.requestToInput(httpRequest);

        // when
        ResponseEntity responseEntity = loginHandler.handle(HttpRequest.from(input));
        Map<String, String> cookies = responseEntity.getCookies();
        String jsessionid = cookies.get("JSESSIONID");
        Session session = SessionManager.findSession(UUID.fromString(jsessionid));

        // then
        assertThat(session).isNotNull();
    }

    @Test
    void 쿠키_없이_로그인에_성공하면_쿠키를_설정해준다() throws IOException {
        // given
        String validAccount = "gugu";
        String validPassword = "password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=" + validAccount + "&password=" + validPassword);

        BufferedReader input = RequestParser.requestToInput(httpRequest);

        // when
        ResponseEntity responseEntity = loginHandler.handle(HttpRequest.from(input));
        String response = responseEntity.generateResponseMessage();

        // then
        assertThat(response).contains(
                "Set-Cookie:",
                "HTTP/1.1 302 "
        );
    }

    @Test
    void 쿠키_있을_때_로그인에_성공하면_쿠키_재발급() throws IOException {
        // given
        String validAccount = "gugu";
        String validPassword = "password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=" + validAccount + "&password=" + validPassword);

        BufferedReader input = RequestParser.requestToInput(httpRequest);

        // when
        ResponseEntity responseEntity = loginHandler.handle(HttpRequest.from(input));
        String response = responseEntity.generateResponseMessage();

        // then
        assertThat(response).contains(
                "Set-Cookie:"
        );
    }

    @Test
    void 로그인에_실패하면_401html_로_리다이렉션() throws IOException {
        // given
        String invalidAccount = "leo";
        String invalidPassword = "password1234";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=" + invalidAccount + "&password=" + invalidPassword);

        BufferedReader input = RequestParser.requestToInput(httpRequest);

        // when
        ResponseEntity responseEntity = loginHandler.handle(HttpRequest.from(input));
        String response = responseEntity.generateResponseMessage();

        // then
        assertThat(response).contains(
                "Location: 401.html",
                "HTTP/1.1 302 "
        );
    }
}
