package nextstep.jwp.web.presentation.controller.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.exception.UnauthorizedException;
import nextstep.jwp.http.header.request.HttpRequest;
import nextstep.jwp.http.header.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostLoginControllerTest {

    @DisplayName("로그인에 성공한다.")
    @Test
    void doService() {
        final PostLoginController postLoginController = new PostLoginController();
        final HttpRequest httpRequest = Fixture.postHttpRequest(
            "/login",
            "account=gugu&password=password"
        );

        final Response response = postLoginController.doService(httpRequest);
        assertThat(response.asString()).contains("302");
    }

    @DisplayName("로그인에 실패한다.")
    @Test
    void doService_invalidValue_fail() {
        final PostLoginController postLoginController = new PostLoginController();
        final HttpRequest httpRequest = Fixture.postHttpRequest(
            "/login",
            "account=gugu2&password=password"
        );

        assertThatThrownBy(() -> postLoginController.doService(httpRequest))
            .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("로그인 기능 컨트롤러 실행 조건을 확인한다.")
    @Test
    void isSatisfiedBy() {
        final PostLoginController postLoginController = new PostLoginController();
        final HttpRequest httpRequest = Fixture.postHttpRequest(
            "/login",
            "account=gugu&password=password&email=hkkang%40woowahan.com"
        );

        assertThat(postLoginController.isSatisfiedBy(httpRequest)).isTrue();
    }
}