package nextstep.jwp.http.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NoResponseBodyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ResponseBodyTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private ResponseBody responseBody;

    @DisplayName("ResponseBody가 있는 경우")
    @Nested
    class ExistResponseBody {

        private static final String RESPONSE_BODY_STRING = "hello world";

        @BeforeEach
        void setUp() {
            responseBody = new ResponseBody(RESPONSE_BODY_STRING);
        }

        @DisplayName("isEmpty 요청시 비어있지 않음을 확인한다.")
        @Test
        void isEmpty() {
            assertThat(responseBody.isEmpty()).isFalse();
        }

        @DisplayName("toString 요청시 개행과 함께 문자열을 반환한다.")
        @Test
        void toStringException() {
            // given
            String expectBodyString = RESPONSE_BODY_STRING;

            // when, then
            assertThat(responseBody.toString()).isEqualTo(expectBodyString);
        }
    }

    @DisplayName("ResponseBody가 없는 경우")
    @Nested
    class NoExistResponseBody {

        @BeforeEach
        void setUp() {
            responseBody = ResponseBody.empty();
        }

        @DisplayName("isEmpty 요청시 비어있음을 확인한다.")
        @Test
        void isEmpty() {
            assertThat(responseBody.isEmpty()).isTrue();
        }

        @DisplayName("toString 요청시 예외가 발생한다.")
        @Test
        void toStringException() {
            assertThatThrownBy(() -> responseBody.toString())
                .isExactlyInstanceOf(NoResponseBodyException.class);
        }
    }
}