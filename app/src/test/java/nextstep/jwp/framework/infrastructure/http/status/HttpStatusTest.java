package nextstep.jwp.framework.infrastructure.http.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HttpStatus 단위 테스트")
class HttpStatusTest {

    @DisplayName("assumeFromHttpStatusPage 메서드는")
    @Nested
    class Describe_assumeFromHttpStatusPage {

        @DisplayName("URL의 ${status code}.html로 끝나면")
        @Nested
        class Context_url_ends_with_status_code_html {

            @DisplayName("그에 맞는 상태를 반환한다.")
            @Test
            void it_returns_matching_status() {
                // given
                String url = "/src/401.html";

                // when
                HttpStatus httpStatus = HttpStatus.assumeFromHttpStatusPage(url);

                // then
                assertThat(httpStatus).isEqualTo(HttpStatus.UNAUTHORIZED);
            }
        }

        @DisplayName("URL의 ${status code}.html로 끝나지 않으면")
        @Nested
        class Context_url_not_ends_with_status_code_html {

            @DisplayName("200 OK를 반환한다.")
            @Test
            void it_returns_200_OK() {
                // given
                String url = "/src/apa.html";

                // when
                HttpStatus httpStatus = HttpStatus.assumeFromHttpStatusPage(url);

                // then
                assertThat(httpStatus).isEqualTo(HttpStatus.OK);
            }
        }
    }
}
