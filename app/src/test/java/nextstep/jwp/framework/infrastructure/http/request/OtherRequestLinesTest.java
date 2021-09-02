package nextstep.jwp.framework.infrastructure.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("OtherRequestLines 단위 테스트")
class OtherRequestLinesTest {

    @DisplayName("from 메서드는")
    @Nested
    class Describe_from {

        @DisplayName("헤더에 Content-Length:가 주어진다면")
        @Nested
        class Context_content_length {

            @DisplayName("값을 저장한다")
            @Test
            void it_saves_length() {
                // given
                List<String> headers = Arrays.asList(
                    "GET /api HTTP/1.1",
                    "Content-Length: 13"
                );

                // when
                OtherRequestLines otherRequestLines = OtherRequestLines.from(headers);

                // then
                assertThat(otherRequestLines.get(HttpHeaders.CONTENT_LENGTH)).isEqualTo("13");
            }
        }

        @DisplayName("헤더의 Content-Length:가 잘못 주어졌다면")
        @Nested
        class Context_invalid_content_length {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_exception() {
                // given
                List<String> headers = Arrays.asList(
                    "GET /api HTTP/1.1",
                    "Content-Length:"
                );

                // when, then
                assertThatCode(() -> OtherRequestLines.from(headers))
                    .isInstanceOf(RuntimeException.class);
            }
        }
    }
}
