package nextstep.org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.common.HttpPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpPathTest {

    @Test
    @DisplayName("HttpPath는 Http Path를 파싱하여 다루는 역할을 한다.")
    void path() {
        final String raw = "/login?account=roma&password=password";

        final HttpPath path = HttpPath.from(raw);

        assertAll(
                () -> assertThat(path.getValue()).isEqualTo("/login"),
                () -> assertThat(path.getParam("account")).isEqualTo("roma"),
                () -> assertThat(path.getParam("password")).isEqualTo("password")
        );
    }
}
