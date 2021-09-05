package nextstep.jwp.framework.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpVersionTest {

    @DisplayName("HttpVersion 찾기 성공")
    @Test
    void from() {
        // when
        HttpVersion httpVersion = HttpVersion.from("HTTP/1.1");

        // then
        assertThat(httpVersion).isSameAs(HttpVersion.HTTP_1_1);
    }

    @DisplayName("HttpVersion 찾기 실패")
    @Test
    void createWithInvalidValue() {
        assertThatThrownBy(() -> HttpVersion.from("HTTP-1.1"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("존재하지 않는 HTTP 버전입니다");
    }
}
