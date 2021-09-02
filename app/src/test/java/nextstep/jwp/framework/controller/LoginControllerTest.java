package nextstep.jwp.framework.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nextstep.common.MockIdGenerator;
import nextstep.common.TestUtil;
import nextstep.jwp.framework.controller.custom.LoginController;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestBody;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestHeader;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.session.HttpSessions;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.web.application.UserService;
import nextstep.jwp.web.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoginController 단위 테스트")
class LoginControllerTest {

    @DisplayName("POST 요청 : Login에 성공하면 index 페이지로 302 리다이렉트되고, Set-Cookie된다.")
    @Test
    void it_redirects_when_login_success() {
        // given
        HttpRequestHeader header = HttpRequestHeader.from(Arrays.asList("POST /login HTTP/1.1"));
        String body = "account=gugu&password=password";
        HttpRequest httpRequest = new HttpRequest(header, new HttpRequestBody(body));
        LoginController loginController = new LoginController(new UserService(), new MockIdGenerator());

        // when
        HttpResponse httpResponse = loginController.doService(httpRequest);

        // then
        assertThat(httpResponse.writeResponseMessage())
            .isEqualTo(TestUtil.writeResponseWithCookie("/index.html", HttpStatus.FOUND));
    }

    @DisplayName("POST 요청 : Login에 실패하면 401 페이지로 이동한다.")
    @Test
    void it_returns_401_when_login_fail() {
        // given
        HttpRequestHeader header = HttpRequestHeader.from(Arrays.asList("POST /login HTTP/1.1"));
        String body = "account=gugadfafdu&password=passwoadfadfrd";
        HttpRequest httpRequest = new HttpRequest(header, new HttpRequestBody(body));
        LoginController loginController = new LoginController(new UserService(), new MockIdGenerator());

        // when
        HttpResponse httpResponse = loginController.doService(httpRequest);

        // then
        assertThat(httpResponse.writeResponseMessage())
            .isEqualTo(TestUtil.writeResponse("/401.html", HttpStatus.UNAUTHORIZED));
    }

    @DisplayName("GET 요청 : 쿠키가 없을 때 로그인 페이지로 이동된다.")
    @Test
    void it_moves_to_login_page_without_cookie() {
        // given
        HttpRequestHeader header = HttpRequestHeader.from(Arrays.asList("GET /login HTTP/1.1"));
        HttpRequest httpRequest = new HttpRequest(header, new HttpRequestBody(null));
        LoginController loginController = new LoginController(new UserService(), new MockIdGenerator());

        // when
        HttpResponse httpResponse = loginController.doService(httpRequest);

        // then
        assertThat(httpResponse.writeResponseMessage())
            .isEqualTo(TestUtil.writeResponse("/login.html", HttpStatus.OK));
    }

    @DisplayName("GET 요청 : 로그인 상태라면 인덱스 페이지로 리다이렉트된다.")
    @Test
    void it_redirects_to_index_page_without_cookie() {
        // given
        List<String> headers = Arrays.asList("GET /login HTTP/1.1", "Cookie: JSESSIONID=1");
        HttpRequest httpRequest =
            new HttpRequest(HttpRequestHeader.from(headers), new HttpRequestBody(""));
        HttpSessions.addSession("1");
        HttpSessions.getSession("1").setAttribute("user", new User(1L, "a", "b", "c"));
        LoginController loginController = new LoginController(new UserService(), new MockIdGenerator());

        // when
        HttpResponse httpResponse = loginController.doService(httpRequest);

        // then
        assertThat(httpResponse.writeResponseMessage())
            .isEqualTo(TestUtil.writeResponse("/index.html", HttpStatus.FOUND));
    }
}
