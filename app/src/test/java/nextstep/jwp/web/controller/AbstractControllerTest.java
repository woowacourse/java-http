package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.Fixture;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AbstractControllerTest {

    private final Controller controller = new AbstractController() {
        @Override
        protected String doGet(HttpRequest httpRequest) {
            return null;
        }

        @Override
        protected String doPost(HttpRequest httpRequest) {
            return null;
        }
    };

    @ParameterizedTest
    @CsvSource({"PUT", "DELETE", "PATCH"})
    void notAllowedMethod(String method) {
        HttpRequest httpRequest = Fixture.httpRequest(method, "");
        assertThatThrownBy(
                () -> controller.doService(httpRequest)
        ).isInstanceOf(MethodNotAllowedException.class);
    }
}
