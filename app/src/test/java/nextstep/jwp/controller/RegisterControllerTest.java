package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.MessageFactory;
import nextstep.jwp.exception.UsernameConflictException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RegisterControllerTest")
class RegisterControllerTest {

    private static final RegisterController REGISTER_CONTROLLER
        = new RegisterController(new RegisterService(), new LoginService());
    private static final String NEW_LINE = "\r\n";

    @Test
    @DisplayName("회원가입 페이지를 반환한다.")
    void doGet() throws IOException {
        // given
        Request request = createRequest(new HashMap<>(), HttpMethod.GET);
        Response response = new Response();

        // when
        REGISTER_CONTROLLER.doGet(request, response);

        // then
        assertThat(response.toString()).hasToString(
            MessageFactory.createResponseOK("register.html", "text/html"));
    }

    @Test
    @DisplayName("회원가입에 성공하면 index.html 로 리다이랙트 시킨다")
    void doPost() {
        // given
        Map<String, String> body = new HashMap<>();
        body.put("account", "test2");
        body.put("password", "password");

        Request request = createRequest(body, HttpMethod.POST);
        Response response = new Response();

        String expected = MessageFactory.createResponseFound("index.html");

        // when
        REGISTER_CONTROLLER.doPost(request, response);

        // then
        for (String message : expected.split(NEW_LINE)) {
            assertThat(response.toString()).contains(message);
        }
    }

    @Test
    @DisplayName("중복된 아이디가 있다면 에러가 발생한다. ")
    void doPostException() {
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");

        Request request = createRequest(body, HttpMethod.POST);
        Response response = new Response();

        assertThatThrownBy(() -> REGISTER_CONTROLLER.doPost(request, response))
            .isInstanceOf(UsernameConflictException.class);
    }

    private Request createRequest(Map<String, String> body, HttpMethod httpMethod) {
        return new Request.Builder()
            .uri("/register")
            .header(new HashMap<>())
            .httpVersion("HTTP/1.1")
            .method(httpMethod)
            .body(body)
            .httpSession(new HttpSession(UUID.randomUUID().toString()))
            .build();
    }
}