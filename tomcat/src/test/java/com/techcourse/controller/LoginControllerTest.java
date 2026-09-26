package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static support.ResponseAssertions.assertHtml;
import static support.ResponseAssertions.assertRedirect;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.view.ResourceRenderer;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestLine;
import org.junit.jupiter.api.Test;

class LoginControllerTest {
    private final LoginController controller = new LoginController(new ResourceRenderer());
    private final HttpResponse response = new HttpResponse();
    private final Session session = new Session(UUID.randomUUID().toString());
    private final String account = UUID.randomUUID().toString();

    @Test
    void 비로그인_상태에서_로그인_화면_응답() throws Exception {
        controller.service(request("GET", ""), response);

        assertHtml(response, "/login.html");
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void 로그인_성공_시_세션에_사용자_저장과_메인_화면으로_리다이렉트() throws Exception {
        User user = new User(account, "password", "user@example.com");
        InMemoryUserRepository.save(user);

        controller.service(request("POST", "account=" + account + "&password=password"), response);

        assertRedirect(response, "/index.html");
        assertThat(session.getAttribute("user")).isSameAs(user);
    }

    @Test
    void 미등록_계정으로_로그인_시_실패_화면으로_리다이렉트() throws Exception {
        assertLoginFailure("account=" + account + "&password=password");
    }

    @Test
    void 비밀번호_불일치_시_실패_화면으로_리다이렉트() throws Exception {
        InMemoryUserRepository.save(new User(account, "password", "user@example.com"));

        assertLoginFailure("account=" + account + "&password=wrong");
    }

    @Test
    void 계정_누락_시_실패_화면으로_리다이렉트() throws Exception {
        assertLoginFailure("password=password");
    }

    @Test
    void 비밀번호_누락_시_실패_화면으로_리다이렉트() throws Exception {
        assertLoginFailure("account=" + account);
    }

    @Test
    void 빈_본문으로_로그인_요청_시_로그인_화면_응답() throws Exception {
        controller.service(request("POST", ""), response);

        assertHtml(response, "/login.html");
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void 로그인_상태에서_GET_요청_시_메인_화면으로_리다이렉트() throws Exception {
        assertAlreadyLoggedIn("GET");
    }

    @Test
    void 로그인_상태에서_POST_요청_시_메인_화면으로_리다이렉트() throws Exception {
        assertAlreadyLoggedIn("POST");
    }

    private void assertAlreadyLoggedIn(String method) throws Exception {
        User user = new User(account, "password", "user@example.com");
        session.setAttribute("user", user);

        controller.service(request(method, "account=unknown&password=wrong"), response);

        assertRedirect(response, "/index.html");
        assertThat(session.getAttribute("user")).isSameAs(user);
    }

    private void assertLoginFailure(String body) throws Exception {
        controller.service(request("POST", body), response);

        assertRedirect(response, "/401.html");
        assertThat(session.getAttribute("user")).isNull();
    }

    private HttpRequest request(String method, String body) {
        var request = new HttpRequest(new RequestLine(method, "/login", "HTTP/1.1"), Map.of(), body);
        request.setSession(session);
        return request;
    }
}
