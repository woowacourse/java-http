package nextstep.jwp.controller;

import nextstep.jwp.http.*;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestHeader;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.jwp.controller.LoginControllerTest.loginRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaticResourceControllerTest {
    protected static final HttpRequest staticResourceRequest = new HttpRequest(
            new HttpRequestHeader(List.of("GET /register HTTP/1.1 ")),
            null
    );

    private final AbstractController staticResourceController = new StaticResourceController();

    @DisplayName("컨트롤러가 해당 요청을 처리할 수 있으면 true, 아니면 false")
    @Test
    void canHandle() {
        assertThat(staticResourceController.canHandle(staticResourceRequest)).isTrue();
        assertThat(staticResourceController.canHandle(loginRequest)).isFalse();
    }

    @DisplayName("get요청을 핸들링 하려하면 exception을 던진다")
    @Test
    void doGet() {
        final HttpResponse actual = staticResourceController.doGet(staticResourceRequest);

        final String url = "/register.html";
        final String responseBody = staticResourceController.readFile(url);
        final HttpStatus httpStatus = HttpStatus.findHttpStatusByUrl(url);

        final HttpResponse expected = new HttpResponse(
                staticResourceRequest.getProtocol(),
                httpStatus,
                ContentType.findByUrl(url),
                responseBody.getBytes().length,
                responseBody);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("post요청을 핸들링 하려하면 exception을 던진다")
    @Test
    void doPost() {
        assertThatThrownBy(() -> staticResourceController.doPost(staticResourceRequest))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
