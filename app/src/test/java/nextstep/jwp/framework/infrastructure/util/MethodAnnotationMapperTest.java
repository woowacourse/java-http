package nextstep.jwp.framework.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import nextstep.jwp.framework.domain.annotation.GetMapping;
import nextstep.jwp.framework.domain.annotation.PostMapping;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MethodAnnotationMapper 단위 테스트")
class MethodAnnotationMapperTest {

    @DisplayName("findMappingClass 메서드는")
    @Nested
    class Describe_findMappingClass {

        @DisplayName("Get이 주어질 때")
        @Nested
        class Context_get {

            @DisplayName("GetMapping 애너테이션을 찾는다.")
            @Test
            void it_returns_getMapping() {
                // given
                HttpMethod httpMethod = HttpMethod.GET;

                // when
                Class<? extends Annotation> mappingClass = MethodAnnotationMapper
                    .findMappingClass(httpMethod);

                // then
                assertThat(mappingClass).isEqualTo(GetMapping.class);
            }
        }

        @DisplayName("Post가 주어질 때")
        @Nested
        class Context_post {

            @DisplayName("PostMapping 애너테이션을 찾는다.")
            @Test
            void it_returns_postMapping() {
                // given
                HttpMethod httpMethod = HttpMethod.POST;

                // when
                Class<? extends Annotation> mappingClass = MethodAnnotationMapper
                    .findMappingClass(httpMethod);

                // then
                assertThat(mappingClass).isEqualTo(PostMapping.class);
            }
        }
    }
}
