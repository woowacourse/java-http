package org.apache.coyote.http11;

import nextstep.jwp.controller.LoginController;
import org.apache.coyote.request.MockRequestReader;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;
import org.apache.exception.FileNotMappingException;
import org.apache.exception.PageRedirectException;
import org.apache.exception.QueryParamsNotFoundException;
import org.apache.exception.RequestBodyNotFoundException;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.coyote.http11.ViewFileFixture.PAGE_404;
import static org.apache.coyote.http11.ViewFileFixture.PAGE_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
public class LoginApiTest {

    @Test
    void 정적_로그인_페이지_요청_성공() {
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);
        assertThat(socket.output())
                .contains(PAGE_LOGIN, "HTTP/1.1 200 OK");
    }

    @Test
    void 쿼리_로그인_요청_성공() {
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);
        assertThat(socket.output())
                .contains(PAGE_LOGIN, "HTTP/1.1 200 OK");
    }

    @Test
    void 존재하지_않는_확장자명으로_요청이_온다면_예외가_발생한다() {
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /login.ht HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        assertThatThrownBy(() -> processor.process(socket))
                .isExactlyInstanceOf(FileNotMappingException.class);
    }

    @Test
    void 존재하지_않는_url로_요청시_404로_연결된다() {
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /loginn HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);
        assertThat(socket.output())
                .contains(PAGE_404, "HTTP/1.1 404 Not Found");
    }

    @Test
    void 쿼리_스트링을_통한_로그인_요청시_필수_쿼리파라미터가_없다면_예외_발생() {
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /login?name=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        assertThatThrownBy(() -> processor.process(socket))
                .isExactlyInstanceOf(QueryParamsNotFoundException.class);
    }

    @Test
    void post_메서드와_requestBody요청시_리퀘스트바디에_필수key가_들어있지_않다면_예외발생() {
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Connection: keep-alive ",
                "Content-Length: 26 ",
                "",
                "name=gugu&name=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        assertThatThrownBy(() -> processor.process(socket))
                .isExactlyInstanceOf(RequestBodyNotFoundException.class);
    }

    @Test
    void post_메서드와_requestBody요청시_리퀘스트바디에_정확한_회원정보가_들어있다면_로그인에_성공한다() throws IOException {
        final List<String> requestLines = new ArrayList<>(Arrays.asList(
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Connection: keep-alive ",
                "Content-Length: 24 ",
                ""));

        final String requestBody = "account=gugu&password=password";
        final Request request = Request.from(new MockRequestReader(requestLines, requestBody));
        final LoginController loginController = new LoginController();
        final ResponseEntity responseEntity = loginController.handle(request);

        assertThat(responseEntity.toString()).contains(PAGE_LOGIN, "HTTP/1.1 302 Temporary Redirect", "Location: /index.html", "Set-Cookie: ");
    }

    @Test
    void post_메서드와_requestBody요청시_리퀘스트바디에_가입하지_않은_회원정보가_들어있다면_예외발생() throws IOException {
        final List<String> requestLines = new ArrayList<>(Arrays.asList(
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Connection: keep-alive ",
                "Content-Length: 24 ",
                ""));

        final String requestBody = "account=gugu&password=password123";
        final Request request = Request.from(new MockRequestReader(requestLines, requestBody));
        final LoginController loginController = new LoginController();

        assertThatThrownBy(() -> loginController.handle(request))
                .isExactlyInstanceOf(PageRedirectException.Unauthorized.class);
    }
}
