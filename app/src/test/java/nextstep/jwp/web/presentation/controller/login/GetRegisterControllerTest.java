package nextstep.jwp.web.presentation.controller.login;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.Response;
import org.junit.jupiter.api.Test;

class GetRegisterControllerTest {

    @Test
    void doService() {
        final GetRegisterController getRegisterController = new GetRegisterController();
        final HttpRequest httpRequest = Fixture.getHttpRequest("/register");

        final Response response = getRegisterController.doService(httpRequest);

        assertThat(response.asString()).contains("200");
    }

    @Test
    void isSatisfiedBy() {
        final GetRegisterController getRegisterController = new GetRegisterController();
        final HttpRequest httpRequest = Fixture.getHttpRequest("/register");

        assertThat(getRegisterController.isSatisfiedBy(httpRequest)).isTrue();
    }
}
