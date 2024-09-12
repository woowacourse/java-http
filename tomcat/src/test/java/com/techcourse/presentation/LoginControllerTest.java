package com.techcourse.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeaders;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController loginController = LoginController.getInstance();

    private final Manager manager = SessionManager.getInstance();

    @AfterEach
    void tearDown() {
        manager.clear();
    }

    @Test
    void GET_요청_시_login_경로를_반환한다() {
        // given
        RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        loginController.getLogin(request, response);

        // then
        assertThat(response.getViewName()).isEqualTo("/login");
    }

    @Test
    void GET_요청_시_이미_로그인되어_있을_경우_index_경로로_리다이렉트한다() {
        // given
        Session session = new Session("1234", manager);
        session.setAttribute("user", new User("gugu", "password", "email"));

        RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Cookie", "JSESSIONID=1234"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        loginController.getLogin(request, response);

        // then
        assertThat(response.getViewName()).isEqualTo("/index");
    }

    @Test
    void GET_요청_시_파라미터가_존재할_경우_로그인_시도_후_성공하면_index_경로로_리다이렉트한다() {
        // given
        RequestLine requestLine = new RequestLine("GET /login?account=gugu&password=password HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        loginController.getLogin(request, response);

        // then
        assertThat(response.getViewName()).isEqualTo("/index");
    }

    @Test
    void GET_요청_시_파라미터가_존재할_경우_로그인_시도_후_실패하면_unauthorized_경로로_리다이렉트한다() {
        // given
        RequestLine requestLine = new RequestLine("GET /login?account=gugu&password=wrong HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        loginController.getLogin(request, response);

        // then
        assertThat(response.getViewName()).isEqualTo("/401");
    }

    @Test
    void POST_요청_시_로그인_시도_후_성공하면_세션을_저장하고_index_경로로_리다이렉트한다() {
        // given
        RequestLine requestLine = new RequestLine("POST /login HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", "30"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from("account=gugu&password=password");
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        loginController.postLogin(request, response);

        // then
        // todo session
        assertThat(response.getViewName()).isEqualTo("/index");
    }

    @Test
    void POST_요청_시_로그인_시도_후_실패하면_unauthorized_경로로_리다이렉트한다() {
        // given
        RequestLine requestLine = new RequestLine("POST /login HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", "30"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from("account=gugu&password=wrong");
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        loginController.postLogin(request, response);

        // then
        assertThat(response.getViewName()).isEqualTo("/401");
    }
}
