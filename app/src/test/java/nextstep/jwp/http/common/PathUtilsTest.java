package nextstep.jwp.http.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class PathUtilsTest {

    @DisplayName("파일의 경로가 들어오면 File을 반환한다.")
    @ParameterizedTest
    @CsvSource({"/401.html", "/css/styles.css", "js/scripts.js"})
    void toFile(String path) {
        final File file = PathUtils.toFile(path);

        String actual = file.getAbsoluteFile().getPath();
        actual = actual.replace("\\", "/");
        assertThat(actual).endsWith(path);
    }
}
