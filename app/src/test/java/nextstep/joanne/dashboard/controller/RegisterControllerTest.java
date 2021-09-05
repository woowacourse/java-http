package nextstep.joanne.dashboard.controller;

import nextstep.joanne.dashboard.exception.MethodArgumentNotValidException;
import nextstep.joanne.dashboard.service.RegisterService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterControllerTest {

    private final RegisterController registerController
            = new RegisterController(new RegisterService());

    @DisplayName("Register에 대한 POST 요청을 한다.")
    @Test
    void doPost() {
        // given
        HashMap<String, String> body = new HashMap<>();
        body.put("email", "joanne@woowahan.com");
        body.put("account", "joanne");
        body.put("password", "password");
        HttpRequest httpRequest =
                new HttpRequest(
                        new RequestLine(HttpMethod.POST, "/register", null),
                        new Headers(new HashMap<>()),
                        new MessageBody(body)
                );
        HttpResponse httpResponse = new HttpResponse();

        // when
        registerController.doPost(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getStatus()).isEqualTo("302");
        assertThat(httpResponse.getHeaders()).isEqualTo(
                "Location: /index.html " + "\r\n" +
                        "Content-Type: text/html \r\n"
        );
    }

    @DisplayName("Register에 대한 POST 요청을 한다. - 실패, email이 null")
    @Test
    void doPostEmailNull() {
        // given
        HashMap<String, String> body = new HashMap<>();
        body.put("email", null);
        body.put("account", "joanne");
        body.put("password", "password");
        HttpRequest httpRequest =
                new HttpRequest(
                        new RequestLine(HttpMethod.POST, "/register", null),
                        new Headers(new HashMap<>()),
                        new MessageBody(body)
                );
        HttpResponse httpResponse = new HttpResponse();

        // when - then
        assertThatThrownBy(() -> registerController.doPost(httpRequest, httpResponse))
                .isInstanceOf(MethodArgumentNotValidException.class);
    }

    @DisplayName("Register에 대한 POST 요청을 한다. - 실패, account가 null")
    @Test
    void doPostAccountNull() {
        // given
        HashMap<String, String> body = new HashMap<>();
        body.put("email", "joanne@woowahan.com");
        body.put("account", null);
        body.put("password", "password");
        HttpRequest httpRequest =
                new HttpRequest(
                        new RequestLine(HttpMethod.POST, "/register", null),
                        new Headers(new HashMap<>()),
                        new MessageBody(body)
                );
        HttpResponse httpResponse = new HttpResponse();

        // when - then
        assertThatThrownBy(() -> registerController.doPost(httpRequest, httpResponse))
                .isInstanceOf(MethodArgumentNotValidException.class);
    }

    @DisplayName("Register에 대한 POST 요청을 한다. - 실패, password가 null")
    @Test
    void doPostPasswordNull() {
        // given
        HashMap<String, String> body = new HashMap<>();
        body.put("email", "joanne@woowahan.com");
        body.put("account", "joanne");
        body.put("password", null);
        HttpRequest httpRequest =
                new HttpRequest(
                        new RequestLine(HttpMethod.POST, "/register", null),
                        new Headers(new HashMap<>()),
                        new MessageBody(body)
                );
        HttpResponse httpResponse = new HttpResponse();

        // when - then
        assertThatThrownBy(() -> registerController.doPost(httpRequest, httpResponse))
                .isInstanceOf(MethodArgumentNotValidException.class);
    }

    @DisplayName("Register에 대해 GET 요청을 한다.")
    @Test
    void doGet() {
        // given
        HttpRequest httpRequest =
                new HttpRequest(
                        new RequestLine(HttpMethod.POST, "/register", null),
                        new Headers(new HashMap<>()),
                        new MessageBody(new HashMap<>())
                );
        HttpResponse httpResponse = new HttpResponse();

        // when
        registerController.doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getStatus()).isEqualTo("200");
        assertThat(httpResponse.getHeaders()).isEqualTo(
                "Content-Type: text/html \r\n" +
                        "Content-Length: 4359 \r\n"
        );
    }
}