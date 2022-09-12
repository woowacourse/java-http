package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceTest {

    @Test
    @DisplayName("지정된 경로의 파일을 읽어온다.")
    void readFileAt() {
        // when
        Resource resource = Resource.from("/test.txt");

        // then
        assertThat(resource.getValue()).contains("test file");
    }

    @Test
    @DisplayName("지정된 경로에 해당하는 파일이 없는 경우 404.html을 읽어온다.")
    void readFileAtButNotFound() {
        // when
        Resource resource = Resource.from("/invalidPath");

        // then
        assertThat(resource.getValue()).contains("test 404.html");
    }
}