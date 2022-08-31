package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class StaticFileTest {

    @DisplayName("입력된 url의 정적 파일을 반환한다.")
    @ParameterizedTest
    @CsvSource({"index.html,index.html", "notFoundUrl.html,404.html"})
    void findByUrl(String url, String expected) {
        // when
        File file = StaticFile.findByUrl(url);

        // then
        assertThat(file.getName()).isEqualTo(expected);
    }
}
