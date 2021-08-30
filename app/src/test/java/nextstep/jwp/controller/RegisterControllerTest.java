package nextstep.jwp.controller;

import nextstep.jwp.http.*;
import nextstep.jwp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.jwp.controller.StaticResourceControllerTest.staticResourceRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterControllerTest {
    protected static final HttpRequest registerRequest = new HttpRequest(
            new HttpRequestHeader(List.of("POST /register HTTP/1.1 ")),
            new HttpRequestBody("account=sakjung&password=password&email=sakjung%40sakjung.com")
    );

    private final Controller registerController = new RegisterController(new UserService());

    @DisplayName("컨트롤러가 해당 요청을 처리할 수 있으면 true, 아니면 false")
    @Test
    void canHandle() {
        assertThat(registerController.canHandle(registerRequest)).isTrue();
        assertThat(registerController.canHandle(staticResourceRequest)).isFalse();
    }

    @DisplayName("get요청을 핸들링 하려하면 exception을 던진다")
    @Test
    void doGet() {
        assertThatThrownBy(() -> registerController.doGet(registerRequest))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @DisplayName("post요청을 성공적으로 핸들링 하면 index.html페이지를 반환한다")
    @Test
    void doPost_success() {
        final HttpResponse actual = registerController.doPost(registerRequest);

        final String redirectUrl = "/index.html";
        final HttpResponse expected = new HttpResponse(
                registerRequest.getProtocol(),
                HttpStatus.SEE_OTHER,
                redirectUrl
        );

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
