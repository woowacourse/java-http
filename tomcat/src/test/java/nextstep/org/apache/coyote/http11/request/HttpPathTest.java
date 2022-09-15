package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.request.HttpPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpPathTest {

    @DisplayName("uri를 받아서 http 경로를 파싱 및 생성한다")
    @Test
    void createHttpPath() {
        final String uri = "/register";

        assertThat(HttpPath.from(uri).getPath()).isEqualTo("/register.html");
    }

    @DisplayName("확장자 없이 경로를 조회할 수 있다")
    @Test
    void getPathWithOurExtension() {
        final HttpPath httpPath = HttpPath.from("/register.html");

        assertThat(httpPath.getPathWithOutExtension()).isEqualTo("/register");
    }
}
