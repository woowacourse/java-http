package org.apache.coyote.httpresponse.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
class LoginHandlerTest extends HandlerTestSupport {

    @Test
    void 올바른_계정으로_request_body로_login_하면_index_페이지로_리다이렉트_한다() {
        // given
        final String input = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final LoginHandler loginHandler = new LoginHandler();

        // when
        final String jSessionId = "bebe-ditoo";
        final HttpRequest spyHttpRequest = spy(httpRequest);
        final Session expectedSession = new Session(jSessionId);
        when(spyHttpRequest.getSession(true)).thenReturn(expectedSession);

        final HttpResponse httpResponse = loginHandler.handle(spyHttpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 302 Found",
                "Content-Type: text/html;charset=utf-8",
                "Location: /index.html",
                "Set-Cookie: JSESSIONID=" + jSessionId);

        // then
        assertThat(actual).contains(expectedHeaders);
    }

    @Test
    void 잘못된_계정으로_request_body로_login_하면_401_페이지로_응답_한다() {
        // given
        final String input = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "Content-Length: 30",
                "",
                "account=gugu&password=dddd");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final LoginHandler loginHandler = new LoginHandler();

        // when
        final HttpResponse httpResponse = loginHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 401 Unauthorized",
                "Content-Type: text/html;charset=utf-8");

        // then
        assertThat(actual).contains(expectedHeaders);
    }

    @Test
    void 로그인된_상태가_아니라면_login_페이지로_이동할_수_있다() {
        // given
        final String input = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final LoginHandler loginHandler = new LoginHandler();

        // when

        final HttpResponse httpResponse = loginHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8");

        // then
        assertThat(actual).contains(expectedHeaders);
    }

    @Test
    void 로그인된_상태라면_login_페이지로_이동할_때_index_페이지로_리다이렉트_된다() {
        // given
        final String jSessionId = "bebe-ditoo";
        final User user = new User("bebe", "ditoo", "bebe@gmail.com");
        InMemoryUserRepository.save(user);

        final String input = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "Cookie: JSESSIONID=" + jSessionId,
                "");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final LoginHandler loginHandler = new LoginHandler();

        // when
        final HttpRequest spyHttpRequest = spy(httpRequest);
        final Session expectedSession = new Session(jSessionId);
        expectedSession.setAttribute("user", user);
        when(spyHttpRequest.getSession(true)).thenReturn(expectedSession);

        final HttpResponse httpResponse = loginHandler.handle(spyHttpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 302 Found",
                "Content-Type: text/html;charset=utf-8",
                "Location: /index.html");

        // then
        assertThat(actual).contains(expectedHeaders);
    }

    @Test
    void login_페이지로_이동할_때_query_string으로_로그인하면_index_페이지로_리다이렉트_된다() {
        // given
        final String input = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final LoginHandler loginHandler = new LoginHandler();

        // when
        final HttpResponse httpResponse = loginHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 302 Found",
                "Content-Type: text/html;charset=utf-8",
                "Location: /index.html");

        // then
        assertThat(actual).contains(expectedHeaders);
    }

    @Test
    void 지원하지_않는_http_method로_로그인을_요청하면_405_페이지를_보여준다() {
        // given
        final String input = String.join("\r\n",
                "DELETE /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final LoginHandler loginHandler = new LoginHandler();

        // when
        final HttpResponse httpResponse = loginHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 405 Method Not Allowed",
                "Content-Type: text/html;charset=utf-8"
        );

        // then
        assertThat(actual).contains(expectedHeaders);
    }
}
