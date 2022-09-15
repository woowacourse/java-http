package org.richard.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ResourceUtilsTest {

    @DisplayName("createResourceAsString 메서드는")
    @Nested
    class createResourceAsString {

        @DisplayName("resource 이하 경로에서 명시된 정적 자료를 String 형태로 반환한다")
        @Test
        void createResourceAsString() throws IOException {
            // given
            final String resourceName = "static/401.html";
            final Path expectedPath = Path.of(getClass().getClassLoader().getResource(resourceName).getPath());
            final String expected = new String(Files.readAllBytes(expectedPath));

            // when
            final String actual = ResourceUtils.createResourceAsString(resourceName);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("resource 이하 경로에서 해당 파일을 찾지 못할 경우 예외를 반환한다")
        @Test
        void throw_exception_on_no_file() {
            // given
            final String resourceName = "static/4011.html";

            // when & then
            assertThatThrownBy(() -> ResourceUtils.createResourceAsString(resourceName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("resource does not exists");
        }
    }
}
