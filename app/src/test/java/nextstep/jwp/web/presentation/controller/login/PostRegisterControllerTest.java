package nextstep.jwp.web.presentation.controller.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.exception.BadRequestException;
import nextstep.jwp.http.header.request.HttpRequest;
import nextstep.jwp.http.header.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostRegisterControllerTest {

    @DisplayName("회원가입에 성공한다.")
    @Test
    void doService() {
        final PostRegisterController postRegisterController = new PostRegisterController();

        final HttpRequest httpRequest = Fixture.postHttpRequest(
            "/register",
            "account=gugu&password=password&email=hkkang%40woowahan.com"
        );

        final Response response = postRegisterController.doService(httpRequest);

        assertThat(response.asString()).contains("302");
    }

    @DisplayName("회원가입에 실패한다.")
    @Test
    void doService_badParams_fail() {
        final PostRegisterController postRegisterController = new PostRegisterController();

        final HttpRequest httpRequest = Fixture.postHttpRequest(
            "/register",
            "account=gugu&email=hkkang%40woowahan.com"
        );

        assertThatThrownBy(() -> postRegisterController.doService(httpRequest))
            .isInstanceOf(BadRequestException.class);
    }

    @DisplayName("회원가입 컨트롤러 조건을 확인한다.")
    @Test
    void isSatisfiedBy() {
        final PostRegisterController postRegisterController = new PostRegisterController();
        final HttpRequest httpRequest = Fixture.postHttpRequest(
            "/register",
            "account=gugu&password=password&email=hkkang%40woowahan.com"
        );

        assertThat(postRegisterController.isSatisfiedBy(httpRequest)).isTrue();
    }
}
