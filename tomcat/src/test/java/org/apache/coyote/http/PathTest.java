package org.apache.coyote.http;

import org.apache.coyote.http.request.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTest {

    @Test
    @DisplayName("")
    void validatePath1() {
        String test = "";

        assertThatThrownBy(() -> new Path(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path cannot be null or empty");
    }

    @Test
    @DisplayName("")
    void validatePath2() {
        String test = "index.html";

        assertThatThrownBy(() -> new Path(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path must start with '/'");
    }

    @Test
    @DisplayName("")
    void parameter() {
        String test = "/login?account=gugu&password=password";

        Path path = new Path(test);

        assertThat(path.getParameters()).hasSize(2);
        assertThat(path.getUri()).isEqualTo("/login");
        assertThat(path.getParameters().get("account")).isEqualTo("gugu");
    }
}
