package nextstep.jwp.framework.infrastructure.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.jwp.framework.domain.annotation.Controller;
import nextstep.jwp.framework.domain.annotation.GetMapping;
import nextstep.jwp.framework.infrastructure.util.MethodAnnotationMapper;
import nextstep.jwp.framework.domain.annotation.PostMapping;
import nextstep.jwp.framework.infrastructure.adapter.post.LoginRequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.get.NotFoundRequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.get.PageGetRequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.post.RegisterRequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.get.StaticRequestAdapter;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;
import nextstep.jwp.framework.infrastructure.util.ApplicationContextLoader;

public class HttpRequestMapping implements RequestMapping {

    private static final List<Class<?>> BEAN_CLASSES =
        ApplicationContextLoader.loadBeans("nextstep");

    @Override
    public RequestAdapter findAdapter(HttpRequest httpRequest) {
        if (httpRequest.isRequestingStaticFiles()) {
            return new StaticRequestAdapter(StaticFileResolver.getInstance());
        }
        return searchHttpRequestAdapter(httpRequest);
    }

    private RequestAdapter searchHttpRequestAdapter(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        HttpMethod httpMethod = httpRequest.getMethod();
        Class<? extends Annotation> mappingAnnotation =
            MethodAnnotationMapper.findMappingClass(httpMethod);
        List<Class<?>> controllers = BEAN_CLASSES.stream()
            .filter(bean -> bean.isAnnotationPresent(Controller.class))
            .collect(Collectors.toList());
        for (Class<?> controller : controllers) {
            Optional<Method> target = Arrays.stream(controller.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(mappingAnnotation))
                .filter(method -> isMappingUrlCorrect(url, method, mappingAnnotation))
                .findAny();
            if (target.isPresent()) {
                return selectProperAdapter(httpRequest, controller, target.get());
            }
        }
        return new NotFoundRequestAdapter(StaticFileResolver.getInstance());
    }

    private RequestAdapter selectProperAdapter(
        HttpRequest httpRequest,
        Class<?> controller,
        Method method
    ) {
        HttpMethod httpMethod = httpRequest.getMethod();
        String url = httpRequest.getUrl();
        if (httpMethod.equals(HttpMethod.GET)) {
            return new PageGetRequestAdapter(controller, method, StaticFileResolver.getInstance());
        }
        if (httpMethod.equals(HttpMethod.POST) && url.equals("/login")) {
            return new LoginRequestAdapter(controller, method, StaticFileResolver.getInstance());
        }
        if (httpMethod.equals(HttpMethod.POST) && url.equals("/register")) {
            return new RegisterRequestAdapter(controller, method, StaticFileResolver.getInstance());
        }
        return new NotFoundRequestAdapter(StaticFileResolver.getInstance());
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
