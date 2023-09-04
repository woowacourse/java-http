package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.Status;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTest {

    @DisplayName("로그인에 성공하면 index.html로 리다이렉트한다.")
    @Test
    void loginSuccess() {
        Response response = UserController.login(
                Request.from("post", "/login", new Headers(), "account=gugu&password=password"));

        assertThat(response.getStatus()).isEqualTo(Status.FOUND);
        assertThat(response.getLocation()).isEqualTo("/index.html");
    }

    @DisplayName("로그인에 실패하면 401.html로 리다이렉트한다.")
    @Test
    void loginFail() {
        Response response = UserController.login(
                Request.from("post", "/login", new Headers(), "account=dodo&password=password"));

        assertThat(response.getStatus()).isEqualTo(Status.FOUND);
        assertThat(response.getLocation()).isEqualTo("/401.html");
    }
}
