package nextstep.jwp.framework.infrastructure.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.common.TestUtil;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("StaticFileResolver 단위 테스트")
class StaticFileResolverTest {

    private final StaticFileResolver staticFileResolver = StaticFileResolver.getInstance();

    @DisplayName("render 메서드는")
    @Nested
    class Describe_render {

        @DisplayName("유효한 요청 URL과 응답 상태가 주어지면")
        @Nested
        class Context_valid_request_url_and_status {

            @DisplayName("해당하는 정적 파일을 찾아 응답을 작성한다.")
            @Test
            void it_returns_response() {
                // given
                HttpRequest html = HttpRequest.ofStaticFile("/index.html");
                HttpRequest css = HttpRequest.ofStaticFile("/assets/chart-area.js");

                // when
                HttpResponse response1 = staticFileResolver.render(html, HttpStatus.OK);
                HttpResponse response2 = staticFileResolver.render(css, HttpStatus.NOT_FOUND);

                // then
                assertThat(response1.getResponseBody())
                    .isEqualTo(TestUtil.writeResponse("/index.html", HttpStatus.OK));
                assertThat(response2.getResponseBody())
                    .isEqualTo(TestUtil.writeResponse("/assets/chart-area.js", HttpStatus.NOT_FOUND));
            }
        }

        @DisplayName("유효하지 않은 요청 URL이 주어지면")
        @Nested
        class Context_invalid_request_url {

            @DisplayName("404 페이지와 응답 코드를 반환한다.")
            @Test
            void it_returns_404() {
                // given
                HttpRequest html = HttpRequest.ofStaticFile("/afdaf.html");

                // when
                HttpResponse render = staticFileResolver.render(html, HttpStatus.NOT_FOUND);

                // then
                assertThat(render.getResponseBody())
                    .isEqualTo(TestUtil.writeResponse("/404.html", HttpStatus.NOT_FOUND));
            }
        }
    }

    @DisplayName("renderDefaultViewByStatus 메서드는")
    @Nested
    class Describe_renderDefaultViewByStatus {

        @DisplayName("응답 상태 코드가 주어지면")
        @Nested
        class Context_httpStatus {

            @DisplayName("그에 맞는 에러 페이지를 반환한다.")
            @Test
            void it_returns_matching_error_page() {
                // given, when
                HttpResponse httpResponse =
                    staticFileResolver.renderDefaultViewByStatus(HttpStatus.INTERNAL_SEVER_ERROR);

                // then
                assertThat(httpResponse.getResponseBody())
                    .isEqualTo(TestUtil.writeResponse("/500.html", HttpStatus.INTERNAL_SEVER_ERROR));
            }
        }
    }
}
