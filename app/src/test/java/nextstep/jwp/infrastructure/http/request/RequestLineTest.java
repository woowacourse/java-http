package nextstep.jwp.infrastructure.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("요청 라인 테스트")
    @Test
    void of() {
        final RequestLine line = RequestLine.of("POST /register HTTP/1.1");

        assertThat(line.getMethod()).isEqualTo(Method.POST);
        assertThat(line.getUri()).isEqualTo(new URI("/register"));
        assertThat(line.getHttpVersion()).isEqualTo("HTTP/1.1");
    }

    @DisplayName("잘못된 라인 형식일 경우 예외가 발생한다.")
    @Test
    void invalidOf() {
        assertThatIllegalArgumentException().isThrownBy(() -> RequestLine.of("POST /register"));
    }
}