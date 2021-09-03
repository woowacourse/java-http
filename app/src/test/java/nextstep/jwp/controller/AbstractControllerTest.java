package nextstep.jwp.controller;

import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class AbstractControllerTest {

/*    @DisplayName("로그인 된 유저인지 확인 한다.")
    @Test
    void isLogin() {
        String uuid = UUID.randomUUID().toString();
        HttpSession session = HttpSessions.getSession(uuid);
        session.setAttribute("user", new User("test", "test", "test@test.com"));

        LoginController loginController = new LoginController(new UserService());

        Assertions.assertThat(loginController.isLogin(session)).isTrue();
    }*/
}