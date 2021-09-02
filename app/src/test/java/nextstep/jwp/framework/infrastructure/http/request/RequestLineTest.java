package nextstep.jwp.framework.infrastructure.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RequestLine 단위 테스트")
class RequestLineTest {

    @DisplayName("from 메서드는")
    @Nested
    class Describe_from {

        @DisplayName("주어진 요청 라인의 포맷이 METHOD_URL_PROTOCOL 형태면")
        @Nested
        class Context_valid_header_start_line {

            @DisplayName("해당 정보를 파싱하여 저장한다.")
            @Test
            void it_saves_method_url_protocol() {
                // given
                String line = "GET /api/post HTTP/1.1";

                // when
                RequestLine requestLine = RequestLine.from(line);

                // then
                assertThat(requestLine)
                    .extracting("httpMethod", "url", "protocol", "queryParameters")
                    .containsExactly(HttpMethod.GET, "/api/post", Protocol.HTTP1_1, new HashMap<>());
            }
        }

        @DisplayName("주어진 HTTP 헤더 첫 번째 줄의 형식이 METHOD_URL_PROTOCOL가 아니면")
        @Nested
        class Context_invalid_header_start_line {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_returns_exception() {
                // given
                String line = "GET HTTP/1.1";

                // when, then
                assertThatCode(() -> RequestLine.from(line))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Invalid Http Request Line");
            }
        }

        @DisplayName("쿼리 스트링이 존재한다면")
        @Nested
        class Context_query_string {

            @DisplayName("정상적으로 파싱한다.")
            @Test
            void it_parses_query_string() {
                // given
                String line = "POST /api/posts?id=kevin&password=123 HTTP/1.1";

                // when
                RequestLine requestLine = RequestLine.from(line);
                Map<String, String> queryParameters = requestLine.getQueryParameters();

                // then
                assertThat(queryParameters)
                    .containsEntry("id", "kevin")
                    .containsEntry("password", "123");
            }
        }

        @DisplayName("값이 없는 쿼리 스트링이 존재한다면")
        @Nested
        class Context_query_string_with_empty_value {

            @DisplayName("정상적으로 파싱한다.")
            @Test
            void it_parses_query_string() {
                // given
                String line = "POST /api/posts?id=kevin&password= HTTP/1.1";

                // when
                RequestLine requestLine = RequestLine.from(line);
                Map<String, String> queryParameters = requestLine.getQueryParameters();

                // then
                assertThat(queryParameters)
                    .containsEntry("id", "kevin")
                    .containsEntry("password", "");
            }
        }
    }
}
