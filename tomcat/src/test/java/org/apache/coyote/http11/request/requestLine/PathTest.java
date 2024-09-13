package org.apache.coyote.http11.request.requestLine;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathTest {

    @DisplayName("root path는 index.html로 변경된다.")
    @Test
    void change_to_index_html_When_root_path() {
        assertThat(new Path("/").getValue()).isEqualTo("/index.html");
    }
}
