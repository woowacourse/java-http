package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotImplementedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MethodTest {

    @DisplayName("이름을 통한 Method 조회시")
    @Nested
    class matchOf {

        @DisplayName("조회에 성공하면 Method를 반환한다.")
        @Test
        void success() {
            // when
            Method GET = Method.matchOf("GET");
            Method POST = Method.matchOf("POST");

            // then
            assertThat(GET).isEqualTo(Method.GET);
            assertThat(POST).isEqualTo(Method.POST);
        }

        @DisplayName("조회에 실패시 예외가 발생한다.")
        @Test
        void exception() {
            assertThatThrownBy(() -> Method.matchOf("noneMethod")).isExactlyInstanceOf(
                NotImplementedException.class);
        }

        @DisplayName("대소문자를 구분한다.")
        @Test
        void matchOfUpperLower() {
            assertThat(Method.matchOf("GET")).isEqualTo(Method.GET);
            assertThatThrownBy(() -> Method.matchOf("get")).isExactlyInstanceOf(
                NotImplementedException.class);
        }
    }
}
