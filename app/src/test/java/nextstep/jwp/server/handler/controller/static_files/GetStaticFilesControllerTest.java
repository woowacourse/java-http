package nextstep.jwp.server.handler.controller.static_files;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetStaticFilesControllerTest {

    @DisplayName("정적 페이지를 불러온다")
    @Test
    void doService() {
        final GetStaticFilesController getStandardController = new GetStaticFilesController();
        final HttpResponse httpResponse =
            getStandardController.doService(Fixture.getHttpRequest("index.html"));

        assertThat(httpResponse.asString()).contains("대시보드");
    }

    @DisplayName("컨트롤러 실행 조건을 확인한다.")
    @Test
    void isSatisfiedBy() {
        final GetStaticFilesController getStandardController = new GetStaticFilesController();

        final boolean actual =
            getStandardController.isSatisfiedBy(Fixture.getHttpRequest("index.html"));

        assertThat(actual).isTrue();
    }
}
