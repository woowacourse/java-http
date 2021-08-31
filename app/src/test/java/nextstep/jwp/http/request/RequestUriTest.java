package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.EmptyQueryParametersException;
import nextstep.jwp.exception.QueryParameterNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestUriTest {

    @DisplayName("QueryParam가 없는 URI 이면")
    @Nested
    class UriQueryParam {

        private final RequestUri requestUri = RequestUri.parse("/path");

        @DisplayName("queryParameters가 없다.")
        @Test
        void hasQueryParam() {
            assertThat(requestUri.hasQueryParam()).isFalse();
        }

        @DisplayName("queryParameter 조회시 예외가 발생한다.")
        @Test
        void getQueryParameterException() {
            assertThatThrownBy(() -> requestUri.getQueryParameter("query"))
                .isExactlyInstanceOf(EmptyQueryParametersException.class);
        }

        @DisplayName("루트 디렉토리(/)가 요청될 경우 /index.html로 치환한다.")
        @Test
        void transferRootDirectory() {
            RequestUri requestUri = RequestUri.parse("/");

            assertThat(requestUri.getValue()).isEqualTo("/index.html");
        }
    }

    @DisplayName("QueryParam을 포함한 URI 이면")
    @Nested
    class UriWithQueryParam {

        private static final String QUERY = "query";
        private static final String PARAMETER = "param";

        private final RequestUri requestUri = RequestUri.parse(
            String.format("/path?%s=%s", QUERY, PARAMETER));

        @DisplayName("queryParameters를 가진다.")
        @Test
        void hasQueryParam() {
            assertThat(requestUri.hasQueryParam()).isTrue();
        }

        @DisplayName("queryParameter 조회가 가능하다.")
        @Test
        void getQueryParameter() {
            assertThat(requestUri.getQueryParameter(QUERY)).isEqualTo(PARAMETER);
        }

        @DisplayName("존재하지 않는 queryParameter 조회시 예외가 발생한다.")
        @Test
        void getQueryParameterException() {
            assertThatThrownBy(() -> requestUri.getQueryParameter("nonExistQuery"))
                .isExactlyInstanceOf(QueryParameterNotFoundException.class);
        }
    }
}