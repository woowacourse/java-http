package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.EmptyQueryParametersException;
import nextstep.jwp.exception.QueryParameterNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @DisplayName("Request-Body가 있는 경우")
    @Nested
    class includeBody {

        private final static String QUERY = "aaa";
        private final static String PARAMETER = "bbb";

        private final RequestBody body = RequestBody.parse(String.format("%s=%s", QUERY, PARAMETER));

        @DisplayName("parameter 요청시 결과를 반환한다.")
        @Test
        void getParameter() {
            assertThat(body.getParameter(QUERY)).isEqualTo(PARAMETER);
        }

        @DisplayName("존재하지 않는 parameter 요청시 예외가 발생한다.")
        @Test
        void getParameterException() {
            assertThatThrownBy(() -> body.getParameter("noExistQuery"))
                .isExactlyInstanceOf(QueryParameterNotFoundException.class);
        }
    }

    @DisplayName("Request-Body가 없는 경우")
    @Nested
    class excludeBody {

        private final RequestBody body = RequestBody.empty();

        @DisplayName("parameter 요청시 예외가 발생한다.")
        @Test
        void getParameterException() {
            assertThatThrownBy(() -> body.getParameter("noExistQuery"))
                .isExactlyInstanceOf(EmptyQueryParametersException.class);
        }
    }
}