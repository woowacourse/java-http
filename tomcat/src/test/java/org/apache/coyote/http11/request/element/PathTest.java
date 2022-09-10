package org.apache.coyote.http11.request.element;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PathTest {

    @Test
    void of() {
        Path emptyPath = Path.of("");
        assertThat(emptyPath.getPath()).isEqualTo("");
        Path slashPath = Path.of("/");
        assertThat(slashPath.getPath()).isEqualTo("");
    }
}
