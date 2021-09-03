package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginServiceTest {

    @DisplayName("request 를 통한 user를 찾는다")
    @Test
    void findUser() {
        RequestBody requestBody = new RequestBody("account=gugu");
        LoginService loginService = new LoginService();

        User user = loginService.findUser(requestBody);

        assertThat(user.getAccount()).isEqualTo("gugu");
    }

    @DisplayName("로그인이 되어있는지 확인")
    @Test
    void isLoginUser() {
        HttpSession httpSession = HttpSessions.getSession("1234");
        httpSession.setAttribute("user", InMemoryUserRepository.findByAccount("gugu").orElseThrow());

        LoginService loginService = new LoginService();

        assertThat(loginService.isLoginUser("1234")).isTrue();
        assertThat(loginService.isLoginUser("123")).isFalse();

    }

}
