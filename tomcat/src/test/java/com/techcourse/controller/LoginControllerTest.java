package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.Test;
import org.was.controller.ResponseResult;
import org.was.session.Session;
import org.was.session.SessionManager;

class LoginControllerTest {

    @Test
    void GET_요청할_경우_로그인_페이지_응답결과_반환() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/login", null, "HTTP/1.1");
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), new RequestBody(null));

        LoginController controller = new LoginController();

        // when
        ResponseResult result = controller.doGet(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.OK),
                () -> assertThat(result.getPath()).isEqualTo("/login.html")
        );
    }

    @Test
    void GET_요청시_로그인_세션이_있는_경우_대시보드_페이지로_리다이렉_응답결과_반환() {
        // given
        User user = new User(1L, "ted", "password", "email");
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);

        RequestLine requestLine = new RequestLine("GET", "/login", null, "HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", "JSESSIONID=" + session.getId());
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(headers), new RequestBody(null));

        LoginController controller = new LoginController();

        // when
        ResponseResult result = controller.doGet(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeaders()).containsEntry("Location", "/index.html")
        );
    }

    @Test
    void POST_요청시_로그인에_성공하면_헤더에_쿠키를_삽입하고_대시보드_페이지로_리다이렉트_응답결과_반환() {
        // given
        User user = new User(1L, "ted", "password", "email" );
        InMemoryUserRepository.save(user);

        RequestLine requestLine = new RequestLine("GET", "/login", null, "HTTP/1.1");
        String formData = "account=ted&password=password";
        RequestBody requestBody = new RequestBody(formData);

        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), requestBody);

        LoginController controller = new LoginController();

        // when
        ResponseResult result = controller.doPost(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeaders()).containsEntry("Location", "/index.html"),
                () -> assertThat(result.getHeaders()).containsKey("Set-Cookie" )
        );
    }

    @Test
    void POST_요청시_본문에_폼데이터가_없을경우_400_응답결과_반환() {
        // given
        User user = new User(1L, "ted", "password", "email");
        InMemoryUserRepository.save(user);

        RequestLine requestLine = new RequestLine("GET", "/login", null, "HTTP/1.1");
        RequestBody requestBody = new RequestBody(null);

        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), requestBody);

        LoginController controller = new LoginController();

        // when
        ResponseResult result = controller.doPost(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.BAD_REQUEST);
    }

    @Test
    void POST_요청시_일치하는_사용자가_없을_경우_401페이지로_리다이렉트_응답결과_반환() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/login", null, "HTTP/1.1" );
        String formData = "account=nonExistUser&password=password";
        RequestBody requestBody = new RequestBody(formData);

        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), requestBody);

        LoginController controller = new LoginController();

        // when
        ResponseResult result = controller.doPost(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeaders()).containsEntry("Location", "/401.html" )
        );
    }

    @Test
    void POST_요청시_사용자_비밀번호가_틀릴_경우_401페이지로_리다이렉트_응답결과_반환() {
        // given
        User user = new User(1L, "ted", "password", "email" );
        InMemoryUserRepository.save(user);

        RequestLine requestLine = new RequestLine("GET", "/login", null, "HTTP/1.1" );
        String formData = "account=ted&password=wrongPassword";
        RequestBody requestBody = new RequestBody(formData);

        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), requestBody);

        LoginController controller = new LoginController();

        // when
        ResponseResult result = controller.doPost(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeaders()).containsEntry("Location", "/401.html" )
        );
    }
}
