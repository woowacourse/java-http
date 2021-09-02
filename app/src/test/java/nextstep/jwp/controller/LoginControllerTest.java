package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.MessageFactory;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.utils.FileConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoginControllerTest")
class LoginControllerTest {

    private static final LoginController LOGIN_CONTROLLER = new LoginController(new LoginService());
    private static final String NEW_LINE = "\r\n";

    @Test
    @DisplayName("login page 반환 테스트")
    void doGet() throws IOException {
        // given
        Request request = createRequest(new HashMap<>(), HttpMethod.GET);
        Response response = new Response();

        // when
        LOGIN_CONTROLLER.doGet(request, response);

        // then
        assertThat(response.toString()).hasToString(createResponseOK());
    }

    @Test
    @DisplayName("로그인을 성공하면 index.html 을 반환한다.")
    void doPost() {
        // given
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");

        Request request = createRequest(body, HttpMethod.POST);
        Response response = new Response();

        String expected = MessageFactory.createResponseFound("index.html");

        // when
        LOGIN_CONTROLLER.doPost(request, response);

        // then
        for (String message : expected.split(NEW_LINE)) {
            assertThat(response.toString()).contains(message);
        }
    }

    @Test
    @DisplayName("로그인에 실패하면 에러가 발생한다. ")
    void doPostException() {
        Map<String, String> body = new HashMap<>();
        body.put("account", "error");
        body.put("password", "password");

        Request request = createRequest(body, HttpMethod.POST);
        Response response = new Response();

        assertThatThrownBy(() -> LOGIN_CONTROLLER.doPost(request, response))
            .isInstanceOf(UnauthorizedException.class);
    }

    private Request createRequest(Map<String, String> body, HttpMethod httpMethod) {
        return new Request.Builder()
            .uri("/login")
            .header(new HashMap<>())
            .httpVersion("HTTP/1.1")
            .method(httpMethod)
            .body(body)
            .httpSession(new HttpSession(UUID.randomUUID().toString()))
            .build();
    }

    private String createResponseOK() throws IOException {
        final String responseBody = FileConverter.fileToString("/login.html");

        return String.join(NEW_LINE, "HTTP/1.1 200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: " + responseBody.getBytes().length,
            "",
            responseBody);
    }
}
