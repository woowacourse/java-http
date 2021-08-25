package nextstep.jwp.framework.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ApplicationContextLoader 단위 테스트")
class ApplicationContextLoaderTest {

    @DisplayName("loadBeans 메서드는")
    @Nested
    class Describe_loadBeans {

        @DisplayName("컨트롤러가 존재하는 상위 패키지명이 주어지면")
        @Nested
        class Context_controller_package_name {

            @DisplayName("패키지 하위에 존재하는 Controller 애너테이션 부착 클래스를 탐색한다.")
            @Test
            void it_returns_controller_classes() {
                // given, when
                List<Class<?>> classes =
                    ApplicationContextLoader.loadBeans("nextstep");

                List<String> simpleNames = classes.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.toList());

                // then
                assertThat(simpleNames).containsOnly("FixtureController1", "FixtureController2");
            }
        }

        @DisplayName("컨트롤러가 존재하지 않는 패키지명이 주어지면")
        @Nested
        class Context_given_no_controller_package_name {

            @DisplayName("빈 리스트를 반환한다.")
            @Test
            void it_return_empty_list() {
                // given, when, then
                assertThat(ApplicationContextLoader.loadBeans("static"))
                    .isEmpty();
            }
        }

        @DisplayName("아무 파일이 존재하지 않는 리소스 디렉토리명이 주어지면")
        @Nested
        class Context_no_resource_directory_name {

            @DisplayName("예외를 반환한다.")
            @Test
            void it_throws_exception() {
                // given, when, then
                assertThatCode(() -> ApplicationContextLoader.loadBeans("sabadf"))
                    .isInstanceOf(RuntimeException.class);
            }
        }
    }
}
