package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.utils.FileConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoginControllerTest")
class LoginControllerTest {

    private static final LoginController LOGIN_CONTROLLER = new LoginController();
    private static final String NEW_LINE = "\r\n";

    @Test
    @DisplayName("login page 반환 테스트")
    void doGet() throws IOException {
        // given
        Request request = createRequest(new HashMap<>());
        Response response = new Response();

        // when
        LOGIN_CONTROLLER.doGet(request, response);

        // then
        assertThat(response.toString()).hasToString(createResponseOK());
    }

    private Request createRequest(Map<String, String> body) {
        return new Request.Builder()
            .uri("/login")
            .header(new HashMap<>())
            .httpVersion("HTTP/1.1")
            .method(HttpMethod.GET)
            .body(body)
            .build();
    }

    @Test
    @DisplayName("로그인을 성공하면 index.html 을 반환한다.")
    void doPost() {
        // given
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");

        Request request = createRequest(body);
        Response response = new Response();

        // when
        LOGIN_CONTROLLER.doPost(request, response);

        // then
        assertThat(response.toString()).hasToString(createResponseFound());
    }

    private String createResponseOK() throws IOException {
        final String responseBody = FileConverter.fileToString("/login.html");

        return String.join(NEW_LINE, "HTTP/1.1 200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: " + responseBody.getBytes().length,
            "",
            responseBody);
    }

    private String createResponseFound() {
        return String.join(NEW_LINE,
            "HTTP/1.1 302 Found",
            "Location: /index.html"
        );
    }
}