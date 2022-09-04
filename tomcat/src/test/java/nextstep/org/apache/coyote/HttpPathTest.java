package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.HttpPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpPathTest {

    @Test
    @DisplayName("HttpPath는 Http Path를 파싱하여 다루는 역할을 한다.")
    void path() {
        // given
        final String raw = "/login?account=roma&password=password";

        // when
        final HttpPath path = HttpPath.from(raw);

        // then
        assertAll(
                () -> assertThat(path.getValue()).isEqualTo("/login"),
                () -> assertThat(path.getParam("account")).isEqualTo("roma"),
                () -> assertThat(path.getParam("password")).isEqualTo("password")
        );
    }

    @Test
    @DisplayName("파라미터의 값이 존재하지 않는 Query Param이 들어온다면 빈 문자열로 처리한다.")
    void paramHasNoValue() {
        // given
        final String raw = "/login?account=&password=password";

        // when
        final HttpPath path = HttpPath.from(raw);

        // then
        assertThat(path.getParam("account")).isEmpty();
    }
}
