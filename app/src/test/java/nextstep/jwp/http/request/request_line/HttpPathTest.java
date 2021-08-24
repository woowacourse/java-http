package nextstep.jwp.http.request.request_line;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpPathTest {

    @DisplayName("파일의 경로가 들어오면 File을 반환한다.")
    @ParameterizedTest
    @CsvSource({"/nextstep.txt", "/simpleDirectory/simple.txt", "nextstep.txt"})
    void toFile(String path) throws IOException {
        HttpPath httpPath = new HttpPath(path);
        File file = httpPath.toFile();

        if(path.startsWith("/")) {
            path = path.substring(1);
        }

        String actual = file.getAbsoluteFile().getPath();
        actual = actual.replace("\\", "/");
        assertThat(actual).endsWith(path);
    }

    @DisplayName("URI에 getParameter가 있다면 파싱한다.")
    @Test
    void getParam() {
        final String uri = "/nextstep.txt?test=1&test2=test";
        HttpPath httpPath = new HttpPath(uri);

        assertThat(httpPath.getParam("test").get()).isEqualTo("1");
        assertThat(httpPath.getParam("test2").get()).isEqualTo("test");
    }

    void getPath() {
    }
}