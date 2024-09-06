package org.apache.coyote.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTest {

    @Test
    @DisplayName("경로가 null 이거나 빈 문자열인 경우 예외를 발생한다.")
    void validatePath1() {
        String test = "";

        assertThatThrownBy(() -> new Path(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path cannot be null or empty");
    }

    @Test
    @DisplayName("경로가 '/'로 시작하지 않는 경우 예외를 발생한다.")
    void validatePath2() {
        String test = "index.html";

        assertThatThrownBy(() -> new Path(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path must start with '/'");
    }

    @Test
    @DisplayName("경로와 같이 들어온 query parameter 를 파싱하여 저장한다.")
    void parameter() {
        String test = "/login?account=gugu&password=password";

        Path path = new Path(test);

        assertThat(path.getParameters()).hasSize(2);
        assertThat(path.getUri()).isEqualTo("/login");
        assertThat(path.getParameters().get("account")).isEqualTo("gugu");
    }
}
