package nextstep.jwp.http.controller.custom.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.Response;
import org.junit.jupiter.api.Test;

class GetRegisterControllerTest {

    @Test
    void doService() {
        final GetRegisterController getRegisterController = new GetRegisterController();
        final HttpRequest httpRequest = new HttpRequest(Fixture.getHttpRequest("/register"));

        final Response response = getRegisterController.doService(httpRequest);

        assertThat(response.asString()).contains("200");
    }

    @Test
    void isSatisfiedBy() {
        final GetRegisterController getRegisterController = new GetRegisterController();
        final HttpRequest httpRequest = new HttpRequest(Fixture.getHttpRequest("/register"));

        assertThat(getRegisterController.isSatisfiedBy(httpRequest)).isTrue();
    }
}