package nextstep.jwp.framework.infrastructure.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HttpRequestHeader 단위 테스트")
class HttpRequestHeaderTest {

    @DisplayName("from 메서드는")
    @Nested
    class Describe_from {

        @DisplayName("주어진 HTTP 헤더 중 첫 번째 줄이 METHOD_URL_PROTOCOL 형태면")
        @Nested
        class Context_valid_header_start_line {

            @DisplayName("해당 정보들을 파싱하여 저장한다.")
            @Test
            void it_saves_method_url_protocol() {
                // given
                List<String> headers = Arrays.asList("GET /api/post HTTP/1.1");

                // when
                HttpRequestHeader header = HttpRequestHeader.from(headers);

                // then
                assertThat(header)
                    .extracting("httpMethod", "url", "protocol", "queryParameters", "contentLength")
                    .containsExactly(HttpMethod.GET, "/api/post", Protocol.HTTP1, new HashMap<>(), 0);
            }
        }

        @DisplayName("주어진 HTTP 헤더 첫 번째 줄의 형식이 METHOD_URL_PROTOCOL가 아니면")
        @Nested
        class Context_invalid_header_start_line {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_returns_exception() {
                // given
                List<String> headers = Arrays.asList("GET HTTP/1.1");

                // when, then
                assertThatCode(() -> HttpRequestHeader.from(headers))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Invalid Http Request Start Line");
            }
        }

        @DisplayName("주어진 HTTP 헤더가 빈 리스트라면")
        @Nested
        class Context_header_empty_list {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_returns_exception() {
                // given, when, then
                assertThatCode(() -> HttpRequestHeader.from(Collections.emptyList()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Invalid Http Request Headers");
            }
        }

        @DisplayName("URL에 쿼리 스트링이 존재하면")
        @Nested
        class Context_query_string {

            @DisplayName("해당 부분을 파싱하여 저장한다.")
            @Test
            void it_saves_query_parameters() {
                // given
                List<String> headers = Arrays.asList("GET /api?name=kevin&pass=123 HTTP/1.1");
                Map<String, String> expected = new HashMap<>();
                expected.put("name", "kevin");
                expected.put("pass", "123");

                // when
                HttpRequestHeader header = HttpRequestHeader.from(headers);

                // then
                assertThat(header)
                    .extracting("queryParameters")
                    .isEqualTo(expected);
            }
        }

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
                HttpRequestHeader header = HttpRequestHeader.from(headers);

                // then
                assertThat(header.getContentLength()).isEqualTo(13);
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
                assertThatCode(() -> HttpRequestHeader.from(headers))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Invalid Content Length");
            }
        }
    }

    @DisplayName("isRequestingStaticFiles 메서드는")
    @Nested
    class Describe_isRequestingStaticFiles {

        @DisplayName("요청 URL이 .css, .js, .html 등으로 끝나면")
        @Nested
        class Context_static_file {

            @DisplayName("true를 반환한다.")
            @Test
            void it_returns_true() {
                // given
                List<String> headers = Arrays.asList("GET /index.html HTTP/1.1");
                HttpRequestHeader header = HttpRequestHeader.from(headers);

                // when, then
                assertThat(header.isRequestingStaticFiles()).isTrue();
            }
        }

        @DisplayName("요청 URL이 .css, .js, .html 등으로 끝나지 않으면")
        @Nested
        class Context_not_static_file {

            @DisplayName("false를 반환한다.")
            @Test
            void it_returns_true() {
                // given
                List<String> headers = Arrays.asList("GET /api HTTP/1.1");
                HttpRequestHeader header = HttpRequestHeader.from(headers);

                // when, then
                assertThat(header.isRequestingStaticFiles()).isFalse();
            }
        }
    }
}
