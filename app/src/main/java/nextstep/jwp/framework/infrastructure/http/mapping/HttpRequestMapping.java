package nextstep.jwp.framework.infrastructure.http.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nextstep.jwp.framework.domain.annotation.Controller;
import nextstep.jwp.framework.domain.annotation.GetMapping;
import nextstep.jwp.framework.domain.annotation.MethodAnnotationMapper;
import nextstep.jwp.framework.domain.annotation.PostMapping;
import nextstep.jwp.framework.infrastructure.http.adapter.ErrorViewResolver;
import nextstep.jwp.framework.infrastructure.http.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.http.adapter.StaticViewResolver;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.util.ApplicationContextLoader;

public class HttpRequestMapping implements RequestMapping {

    private static final List<Class<?>> BEAN_CLASSES =
        ApplicationContextLoader.loadBeans("nextstep");

    @Override
    public RequestAdapter findAdapter(HttpRequest httpRequest) {
        if (isOnlyRequestingStaticFile(httpRequest)) {
            return searchAdapter(httpRequest, httpRequest.getMethod());
        }
        return searchAdapter(httpRequest, httpRequest.getMethod());
    }


    private boolean isOnlyRequestingStaticFile(HttpRequest httpRequest) {
        return httpRequest.getMethod().equals(HttpMethod.GET);
    }


    private RequestAdapter searchAdapter(HttpRequest httpRequest, HttpMethod httpMethod) {
        String url = httpRequest.getUrl();
        Class<? extends Annotation> mappingAnnotation =
            MethodAnnotationMapper.findMappingClass(httpMethod);
        for (Class<?> bean : BEAN_CLASSES) {
            if (!bean.isAnnotationPresent(Controller.class)) {
                continue;
            }
            Optional<Method> target = Arrays.stream(bean.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(mappingAnnotation))
                .filter(method -> isMappingUrlCorrect(url, method, mappingAnnotation))
                .findAny();
            if (target.isPresent()) {
                return new StaticViewResolver(bean, target.orElseThrow(IllegalStateException::new));
            }
        }
        return new ErrorViewResolver(HttpStatus.NOT_FOUND);
    }

    private boolean isMappingUrlCorrect(
        String url,
        Method method,
        Class<? extends Annotation> mappingAnnotation
    ) {
        if (mappingAnnotation.equals(GetMapping.class)) {
            return method.getAnnotation(GetMapping.class).value().equals(url);
        }
        return method.getAnnotation(PostMapping.class).value().equals(url);
    }
}
