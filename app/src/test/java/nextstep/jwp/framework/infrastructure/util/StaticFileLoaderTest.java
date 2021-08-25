package nextstep.jwp.framework.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("StaticFileLoader 단위 테스트")
class StaticFileLoaderTest {

    @DisplayName("loadStaticFilePaths 메서드는")
    @Nested
    class Describe_loadStaticFilePaths {

        @DisplayName("정적 파일이 존재하는 리소스 디렉토리명이 주어지면")
        @Nested
        class Context_resource_directory_name {

            @DisplayName("리소스 파일들의 경로를 반환한다.")
            @Test
            void it_returns_static_file_paths() {
                // given, when
                List<Path> paths = StaticFileLoader.loadStaticFilePaths("testatic");
                String name = paths.stream()
                    .map(Path::toString)
                    .collect(Collectors.toList()).get(0);

                assertThat(paths).hasSize(1);
                assertThat(name).endsWith("test.txt");
            }
        }

        @DisplayName("존재하지 않는 리소스 디렉토리명이 주어지면")
        @Nested
        class Context_no_resource_directory_name {

            @DisplayName("예외를 반환한다.")
            @Test
            void it_throws_exception() {
                // given, when, then
                assertThatCode(() -> StaticFileLoader.loadStaticFilePaths("sabadf"))
                    .isInstanceOf(RuntimeException.class);
            }
        }
    }
}
