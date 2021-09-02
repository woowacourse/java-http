package nextstep.jwp.framework.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.common.TestUtil;
import nextstep.jwp.framework.controller.custom.IndexPageController;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("IndexPageController 단위 테스트")
class IndexPageControllerTest {

    @DisplayName("doService 메서드는")
    @Nested
    class Describe_doService {

        @DisplayName("요청이 들어오면")
        @Nested
        class Context_request_accepted {

            @DisplayName("/index.html 페이지로 이동된다.")
            @Test
            void it_returns_index_page() {
                // given
                HttpRequest httpRequest = HttpRequest.ofStaticFile("/");

                // when
                HttpResponse httpResponse = new IndexPageController().doService(httpRequest);

                // then
                assertThat(httpResponse.writeResponseMessage())
                    .isEqualTo(TestUtil.writeResponse("/index.html", HttpStatus.OK));
            }
        }
    }
}
