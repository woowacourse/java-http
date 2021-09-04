package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.EmptyQueryParametersException;
import nextstep.jwp.exception.InvalidHttpRequestException;
import nextstep.jwp.exception.QueryParameterNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    private static final String URI = "/home";

    @DisplayName("Http Request-Line 규격에 맞지 않으면 예외가 발생한다.")
    @Test
    void parse() {
        assertThatThrownBy(() -> RequestLine.parse("GET HTTP/1.1"))
            .isInstanceOf(InvalidHttpRequestException.class);
        assertThatThrownBy(() -> RequestLine.parse("HTTP/1.1"))
            .isInstanceOf(InvalidHttpRequestException.class);
        assertThatThrownBy(() -> RequestLine.parse("GET / dfsdf"))
            .isInstanceOf(InvalidHttpRequestException.class);
    }

    @DisplayName("Method가 같은지 확인한다.")
    @Test
    void isSameMethod() {
        // given
        RequestLine requestLine = RequestLine.parse(String.format("GET %s HTTP/1.1", URI));

        // when, then
        assertThat(requestLine.isSameMethod(Method.GET)).isTrue();
        assertThat(requestLine.isSameMethod(Method.POST)).isFalse();
    }


    @DisplayName("QueryParameter를 포함한 Request-Line의 경우")
    @Nested
    class RequestLineWithQueryParam {

        private static final String QUERY = "what";
        private static final String PARAMETER = "the";

        private final RequestLine requestLine = RequestLine.parse(
            String.format("GET %s?%s=%s HTTP/1.1", URI, QUERY, PARAMETER));

        @DisplayName("QueryParameter를 포함하고 있다.")
        @Test
        void hasQueryParam() {
            assertThat(requestLine.hasQueryParam()).isTrue();
        }

        @DisplayName("uri 요청시 query parameter를 제외한 uri만 반환한다.")
        @Test
        void getUri() {
            assertThat(requestLine.getUri()).isEqualTo(URI);
        }

        @DisplayName("uri parameter 요청시 parameter를 반환한다.")
        @Test
        void getUriParameter() {
            assertThat(requestLine.getUriParameter(QUERY)).isEqualTo(PARAMETER);
        }

        @DisplayName("존재하지 않는 uri parameter 요청시 예외가 발생한다.")
        @Test
        void getUriParameterException() {
            assertThatThrownBy(() -> requestLine.getUriParameter("something"))
                .isInstanceOf(QueryParameterNotFoundException.class);
        }
    }

    @DisplayName("QueryParameter를 포함하지 않은 Request-Line의 경우")
    @Nested
    class RequestLineWithOutQueryParam {

        private final RequestLine requestLine = RequestLine.parse(
            String.format("GET %s HTTP/1.1", URI));

        @DisplayName("QueryParameter를 포함하고 있지 않다.")
        @Test
        void hasQueryParam() {
            assertThat(requestLine.hasQueryParam()).isFalse();
        }

        @DisplayName("uri 요청시 uri를 반환한다.")
        @Test
        void getUri() {
            assertThat(requestLine.getUri()).isEqualTo(URI);
        }

        @DisplayName("uri parameter 요청시 예외가 발생한다.")
        @Test
        void getUriParameterException() {
            assertThatThrownBy(() -> requestLine.getUriParameter("something"))
                .isInstanceOf(EmptyQueryParametersException.class);
        }
    }
}