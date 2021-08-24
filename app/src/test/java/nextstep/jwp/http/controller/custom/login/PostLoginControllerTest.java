package nextstep.jwp.http.controller.custom.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.exception.BadRequestException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostLoginControllerTest {

    @DisplayName("회원가입에 성공한다.")
    @Test
    void doService() {
        final PostLoginController postLoginController = new PostLoginController();
        final HttpRequest httpRequest = new HttpRequest(Fixture.postHttpRequest(
            "/login",
            "account=gugu&password=password&email=hkkang%40woowahan.com"
        ));

        final Response response = postLoginController.doService(httpRequest);
        assertThat(response.asString()).contains("302");
    }

    @DisplayName("회원가입에 실패한다.")
    @Test
    void doService_invalidParams_fail() {
        final PostLoginController postLoginController = new PostLoginController();
        final HttpRequest httpRequest = new HttpRequest(Fixture.postHttpRequest(
            "/login",
            "account=gugu&email=hkkang%40woowahan.com"
        ));

        assertThatThrownBy(() -> postLoginController.doService(httpRequest))
            .isInstanceOf(BadRequestException.class);
    }

    @DisplayName("로그인 기능 컨트롤러 실행 조건을 확인한다.")
    @Test
    void isSatisfiedBy() {
        final PostLoginController postLoginController = new PostLoginController();
        final HttpRequest httpRequest = new HttpRequest(Fixture.postHttpRequest(
            "/login",
            "account=gugu&password=password&email=hkkang%40woowahan.com"
        ));

        assertThat(postLoginController.isSatisfiedBy(httpRequest)).isTrue();
    }
}