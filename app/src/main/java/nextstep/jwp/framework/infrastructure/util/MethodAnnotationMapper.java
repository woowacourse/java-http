package nextstep.jwp.framework.infrastructure.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import nextstep.jwp.framework.domain.annotation.GetMapping;
import nextstep.jwp.framework.domain.annotation.PostMapping;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;

public enum MethodAnnotationMapper {
    GET(HttpMethod.GET, GetMapping.class),
    POST(HttpMethod.POST, PostMapping.class);

    private final HttpMethod httpMethod;
    private final Class<? extends Annotation> mappingClass;

    MethodAnnotationMapper(HttpMethod httpMethod, Class<? extends Annotation> mappingClass) {
        this.httpMethod = httpMethod;
        this.mappingClass = mappingClass;
    }

    public static Class<? extends Annotation> findMappingClass(HttpMethod httpMethod) {
        return Arrays.stream(MethodAnnotationMapper.values())
            .filter(mapper -> mapper.httpMethod.equals(httpMethod))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("해당 메서드와 매치되는 애너테이션이 없습니다."))
            .mappingClass;
    }
}
