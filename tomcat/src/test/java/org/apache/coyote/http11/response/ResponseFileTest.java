package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URL;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseFileTest {

    @DisplayName("경로를 전달받아 파일을 읽어온다.")
    @Test
    void instantiateResponseFile() {
        // given
        URL resourceURL = ResponseFile.class.getResource("/static/index.html");

        // when
        ResponseFile responseFile = ResponseFile.of(Objects.requireNonNull(resourceURL));

        // then
        assertAll(
                () -> assertThat(responseFile).isNotNull(),
                () -> assertThat(responseFile.size()).isEqualTo(5564),
                () -> assertThat(responseFile.getContentType()).isEqualTo("text/html;charset=utf-8")
        );
    }
}
