package nextstep.joanne.dashboard.controller;

import nextstep.joanne.dashboard.service.LoginService;
import nextstep.joanne.server.http.Headers;
import nextstep.joanne.server.http.HttpMethod;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.request.MessageBody;
import nextstep.joanne.server.http.request.RequestLine;
import nextstep.joanne.server.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {
    private final LoginController loginController
            = new LoginController(new LoginService());

    @DisplayName("Login에 대한 POST 요청을 한다.")
    @Test
    void doPost() {
        // given
        HashMap<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");
        HttpRequest httpRequest =
                new HttpRequest(
                        new RequestLine(HttpMethod.POST, "/login", null),
                        new Headers(new HashMap<>()),
                        new MessageBody(body)
                );
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.doPost(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getStatus()).isEqualTo("302");
        assertThat(httpResponse.getHeaders()).endsWith(
                "Location: /index.html " + "\r\n" +
                        "Content-Type: text/html \r\n"
        );
    }

    @DisplayName("Login에 대한 GET 요청을 한다.")
    @Test
    void doGet() {
        // given
        HashMap<String, String> headers = new HashMap<>();
        HttpRequest httpRequest =
                new HttpRequest(
                        new RequestLine(HttpMethod.POST, "/login", null),
                        new Headers(headers),
                        new MessageBody(new HashMap<>())
                );
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getStatus()).isEqualTo("200");
    }
}
