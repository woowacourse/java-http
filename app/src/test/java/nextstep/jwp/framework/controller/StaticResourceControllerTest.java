package nextstep.jwp.framework.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import nextstep.common.TestUtil;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestBody;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestHeader;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StaticResourceController 테스트")
class StaticResourceControllerTest {

    @DisplayName("css, js, html 등 정적 경로의 파일들을 반환한다.")
    @Test
    void it_returns_resource_file() {
        // given
        HttpRequestHeader header =
            HttpRequestHeader.from(Arrays.asList("GET /assets/chart-area.js HTTP/1.1"));
        HttpRequest httpRequest = new HttpRequest(header, new HttpRequestBody(null));

        // when
        HttpResponse httpResponse = new StaticResourceController().doService(httpRequest);

        // then
        assertThat(httpResponse.writeResponseMessage())
            .isEqualTo(TestUtil.writeResponse("/assets/chart-area.js", HttpStatus.OK));
    }

    @DisplayName("404, 401, 500 등 에러 페이지로 가는 경우 상태값 또한 함께 변경된다.")
    @Test
    void it_returns_resource_file_with_proper_status() {
        // given
        HttpRequest httpRequest = HttpRequest.ofStaticFile("/500.html");

        // when
        HttpResponse httpResponse = new StaticResourceController().doService(httpRequest);

        // then
        assertThat(httpResponse.writeResponseMessage())
            .isEqualTo(TestUtil.writeResponse("/500.html", HttpStatus.INTERNAL_SEVER_ERROR));
    }
}
