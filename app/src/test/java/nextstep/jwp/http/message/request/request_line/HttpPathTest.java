package nextstep.jwp.http.message.request.request_line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpPathTest {
    @DisplayName("URI에 getParameter가 있다면 파싱한다.")
    @Test
    void getParam() {
        final String uri = "/nextstep.txt?test=1&test2=test";
        HttpPath httpPath = new HttpPath(uri);

        assertThat(httpPath.getParam("test")).isEqualTo("1");
        assertThat(httpPath.getParam("test2")).isEqualTo("test");
    }
}
