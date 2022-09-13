package nextstep.org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.common.FileReader;
import org.junit.jupiter.api.Test;

public class FileReaderTest {

    @Test
    void read() {
        final String body = FileReader.read("/index.html");

        assertThat(body).isNotNull();
    }

    @Test
    void readNotExistResource() {
        assertThatThrownBy(() -> FileReader.read("/index11.html"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 파일입니다.");
    }
}
