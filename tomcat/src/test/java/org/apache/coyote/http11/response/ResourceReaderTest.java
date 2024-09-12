package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceReaderTest {

    @DisplayName("파일 데이터를 읽어온다.")
    @Test
    void successReadTest() throws IOException {
        String resource = "index.html";

        String content = ResourceReader.read(resource);

        assertNotNull(content);
    }

    @DisplayName("존재하지 않는 파일을 읽어올 경우 예외를 반환한다.")
    @Test
    void failureReadTest() {
        String resource = "jazz.html";

        assertThatThrownBy(() -> ResourceReader.read(resource))
                .isInstanceOf(NullPointerException.class);
    }
}
