package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import nextstep.TestUtil;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseStatus;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @AfterEach
    void tearDown() {
        HttpSessions.clear();
    }

    @DisplayName("로그인 화면을 반환한다.")
    @Test
    void renderPage() {
        AbstractController controller = new LoginController();
        HttpRequest request = TestUtil.createRequest("GET /login HTTP/1.1");

        HttpResponse httpResponse = controller.doGet(request);

        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.OK);
    }

    @DisplayName("로그인이 이미 되어있는 경우 초기화면으로 리다이렉트 한다.")
    @Test
    void setSessionAttribute() {
        AbstractController controller = new LoginController();
        HttpRequest request = TestUtil.createRequest("GET /login HTTP/1.1");
        HttpSession httpSession = TestUtil.addSession(request);
        httpSession.setUser(new User("gugu", "password", "email"));

        HttpResponse httpResponse = controller.doGet(request);

        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/index.html");
    }

    @DisplayName("일치하는 회원정보가 requestBody에 포함된 경우 초기화면으로 리다이렉트한다.")
    @Test
    void loginSuccessRedirect() {
        LoginController controller = new LoginController();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", "gugu");
        requestBody.put("password", "password");

        HttpRequest request = TestUtil.createRequest("POST /login HTTP/1.1", requestBody);
        HttpSession session = TestUtil.addSession(request);

        HttpResponse httpResponse = controller.doPost(request);

        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/index.html");
        assertThat(HttpSessions.getSession(session.getId()).getUser()).isNotNull();
    }

    @DisplayName("잘못된 회원정보가 포함된 경우 에러화면으로 리다이렉트한다.")
    @Test
    void loginFailRedirect() {
        LoginController controller = new LoginController();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", "wrong");
        requestBody.put("password", "wrong");
        HttpRequest request = TestUtil.createRequest("POST /login HTTP/1.1", requestBody);
        TestUtil.addSession(request);

        HttpResponse httpResponse = controller.doPost(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/401.html");
    }
}