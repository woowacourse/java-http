package org.richard.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.config.TomcatConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.config.ApplicationConfig;

class YamlUtilsTest {

    @DisplayName("readPropertyAsObject 메서드는")
    @Nested
    class ReadPropertyAsObject {

        @DisplayName("application.yml 파일 내 basePackage 설정을 읽어들일 수 있다")
        @Test
        void readBasePackage() {
            // given
            final var fileName = "application.yml";
            final var applicationConfigClass = ApplicationConfig.class;
            final var expected = "nextstep";

            // when
            final var applicationConfig = YamlUtils.readPropertyAsObject(fileName, applicationConfigClass);
            final var actual = applicationConfig.getBasePackage();

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("tomcat.yml 파일 내 servletBasePackage 설정을 읽어들일 수 있다")
        @Test
        void readServletBasePackage() {
            // given
            final var fileName = "tomcat.yml";
            final var tomcatConfigClass = TomcatConfig.class;
            final var expected = "org.springframework.servlet";

            // when
            final var applicationConfig = YamlUtils.readPropertyAsObject(fileName, tomcatConfigClass);
            final var actual = applicationConfig.getServletBasePackage();

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("파일이 존재하지 않을 경우 예외를 던진다")
        @Test
        void exception_on_no_file() {
            // given
            final var fileName = "tomca222t.yml";
            final var tomcatConfigClass = TomcatConfig.class;

            // when & then
            assertThatThrownBy(() -> YamlUtils.readPropertyAsObject(fileName, tomcatConfigClass))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Requested file does not exist");
        }
    }
}
