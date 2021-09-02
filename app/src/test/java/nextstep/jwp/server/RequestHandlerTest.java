package nextstep.jwp.server;

import nextstep.jwp.MockSocket;
import nextstep.jwp.server.RequestHandler;
import nextstep.jwp.server.RequestMapping;
import nextstep.jwp.server.http.common.ContentType;
import nextstep.jwp.server.http.response.HttpResponse;
import nextstep.jwp.server.http.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static nextstep.jwp.Fixture.getRequest;
import static nextstep.jwp.Fixture.postFormDataRequest;
import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    @DisplayName("/ 로 요청 시 Hello world를 응답 body로 보낸다.")
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", "12");
        response.write("Hello world!");
        assertThat(socket.output()).isEqualTo(response.getResponse());
    }

    @Test
    @DisplayName("GET /index.html 요청 시 index.html 페이지로 이동한다.")
    void index() {
        // given
        final String httpRequest = getRequest("/index.html");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.forward("/index.html", HttpStatus.OK);
        assertThat(socket.output()).isEqualTo(response.getResponse());
    }

    @Test
    @DisplayName("등록되지 않은 url 요청 시 404 에러를 보낸다.")
    void notExistUrl() {
        // given
        final String httpRequest = getRequest("/oz");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.forward("/404.html", HttpStatus.NOT_FOUND);
        assertThat(socket.output()).isEqualTo(response.getResponse());
    }

    @Test
    @DisplayName("GET /login 요청 시 로그인 페이지로 이동한다.")
    void moveLoginPage() {
        // given
        final String httpRequest = getRequest("/login");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.forward("/login.html", HttpStatus.OK);
        assertThat(socket.output()).isEqualTo(response.getResponse());
    }

    @Test
    @DisplayName("POST /login 요청 시 가입된 account와 password면 index.html로 redirect 된다.")
    void loginSuccess() {
        // given
        final String httpRequest = postFormDataRequest("/login", "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.sendRedirect("/index.html");
        assertThat(socket.output()).contains("Set-Cookie", "JSESSIONID");
        assertThat(socket.output()).contains(response.getResponse());
    }


    @Test
    @DisplayName("POST /login 요청 시 존재하지 않는 account면 401 에러를 보낸다.")
    void loginFailWhenNotExistAccount() {
        // given
        final String httpRequest = postFormDataRequest("/login", "account=oz&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.forward("/401.html", HttpStatus.UNAUTHORIZED);
        assertThat(socket.output()).isEqualTo(response.getResponse());
    }

    @Test
    @DisplayName("POST /login 요청 시 password가 일치하지 않으면 401 에러를 보낸다.")
    void loginFailWhenNotExistPassword() {
        // given
        final String httpRequest = postFormDataRequest("/login", "account=gugu&password=123");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.forward("/401.html", HttpStatus.UNAUTHORIZED);
        assertThat(socket.output()).isEqualTo(response.getResponse());
    }

    @Test
    @DisplayName("GET /register 요청 시 회원가입 페이지로 이동한다.")
    void getRegisterPage() {
        // given
        final String httpRequest = getRequest("/register");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.forward("/register.html", HttpStatus.OK);
        assertThat(socket.output()).isEqualTo(response.getResponse());
    }

    @Test
    @DisplayName("POST /register 요청 시 회원가입이 성공하면 index.html로 redirect 된다.")
    void registerSuccess() {
        // given
        final String httpRequest = postFormDataRequest("/register", "account=oz&password=123");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.sendRedirect("/index.html");
        assertThat(socket.output()).contains(response.getResponse());
    }

    @Test
    @DisplayName("POST /register 요청 시 회원가입이 실패하면 500 에러를 보낸다.")
    void registerFail() {
        // given
        final String httpRequest = postFormDataRequest("/register", "account=gugu&password=123");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.forward("/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(socket.output()).contains(response.getResponse());
    }

    @Test
    @DisplayName("GET /login 요청 시 이미 로그인이 되어있으면 index.html로 리다이렉트 된다.")
    void tryLoginWhenAlreadyLogin() {
        // given
        final String sessionId = 로그인_되어있음();

        final String httpRequest = getRequest("/login", "JSESSIONID=" + sessionId);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        // when
        requestHandler.run();

        // then
        HttpResponse response = new HttpResponse();
        response.sendRedirect("/index.html");
        assertThat(socket.output()).contains(response.getResponse());

    }

    private String 로그인_되어있음() {
        final String httpRequest = postFormDataRequest("/login", "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, new RequestMapping());

        requestHandler.run();

        String[] splitLine = socket.output().split(System.lineSeparator());
        String[] splitCookie = splitLine[splitLine.length - 1].split("=");
        return splitCookie[1];
    }
}
