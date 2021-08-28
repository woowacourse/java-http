package nextstep.jwp.web.presentation.controller.login;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class GetLoginControllerTest {

    @DisplayName("로그인 페이지에 접속한다.")
    @ParameterizedTest
    @MethodSource("parametersForDoService")
    void doService(String uri, int code, String content) {
        final GetLoginController getLoginController = new GetLoginController();
        final HttpRequest httpRequest = Fixture.getHttpRequest(uri);

        final Response response = getLoginController.doService(httpRequest);
        assertThat(response.asString()).contains(String.valueOf(code));
        assertThat(response.asString()).contains(String.valueOf(content));
    }

    private static Stream<Arguments> parametersForDoService() {
        return Stream.of(
            Arguments.of("/login", 200, "로그인"),
            Arguments.of("/login?password=123", 200, "로그인"),
            Arguments.of("/login?account=1", 200, "로그인")
        );
    }

    @DisplayName("로그인 페이지 컨트롤러 실행 조건을 확인한다.")
    @Test
    void isSatisfiedBy() {
        final GetLoginController getLoginController = new GetLoginController();
        final HttpRequest httpRequest = Fixture.getHttpRequest("/login");

        assertThat(getLoginController.isSatisfiedBy(httpRequest)).isTrue();
    }
}
