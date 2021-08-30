package nextstep.jwp.controller;

import nextstep.jwp.http.*;
import nextstep.jwp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.jwp.controller.StaticResourceControllerTest.staticResourceRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginControllerTest {
    protected static final HttpRequest loginRequest = new HttpRequest(
            new HttpRequestHeader(List.of("POST /login HTTP/1.1 ")),
            new HttpRequestBody("account=gugu&password=password")
    );

    private final Controller loginController = new LoginController(new UserService());

    @DisplayName("컨트롤러가 해당 요청을 처리할 수 있으면 true, 아니면 false")
    @Test
    void canHandle() {
        assertThat(loginController.canHandle(loginRequest)).isTrue();
        assertThat(loginController.canHandle(staticResourceRequest)).isFalse();
    }

    @DisplayName("get요청을 핸들링 하려하면 exception을 던진다")
    @Test
    void doGet() {
        assertThatThrownBy(() -> loginController.doGet(loginRequest))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @DisplayName("post요청을 성공적으로 핸들링 하면 index.html페이지를 반환한다")
    @Test
    void doPost_success() {
        final HttpResponse actual = loginController.doPost(loginRequest);

        final String redirectUrl = "/index.html";
        final HttpResponse expected = new HttpResponse(
                loginRequest.getProtocol(),
                HttpStatus.FOUND,
                redirectUrl
        );

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("post요청을 성공적으로 핸들링 하면 index.html페이지를 반환한다")
    @Test
    void doPost_fail() {
        final HttpRequest wrongLoginRequest = new HttpRequest(
                new HttpRequestHeader(List.of("POST /login HTTP/1.1 ")),
                new HttpRequestBody("account=sakjung&password=password")
        );

        final HttpResponse actual = loginController.doPost(wrongLoginRequest);

        final String unauthorizedUrl = "/401.html";
        final String responseBody = loginController.readFile(unauthorizedUrl);

        final HttpResponse expected = new HttpResponse(
                wrongLoginRequest.getProtocol(),
                HttpStatus.findHttpStatusByUrl(unauthorizedUrl),
                ContentType.findByUrl(unauthorizedUrl),
                responseBody.getBytes().length,
                responseBody);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
