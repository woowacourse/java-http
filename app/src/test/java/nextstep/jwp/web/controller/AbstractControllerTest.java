package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.Fixture;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AbstractControllerTest {

    private final Controller controller = new AbstractController() {
        @Override
        protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        }

        @Override
        protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        }
    };

    @ParameterizedTest
    @CsvSource({"PUT", "DELETE", "PATCH"})
    void notAllowedMethod(String method) {
        HttpRequest httpRequest = Fixture.httpRequest(method, "");
        HttpResponse httpResponse = HttpResponse.empty();
        assertThatThrownBy(
                () -> controller.doService(httpRequest, httpResponse)
        ).isInstanceOf(MethodNotAllowedException.class);
    }
}
