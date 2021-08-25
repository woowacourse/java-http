package nextstep.jwp.http.request.request_line;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import nextstep.jwp.http.common.PathUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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