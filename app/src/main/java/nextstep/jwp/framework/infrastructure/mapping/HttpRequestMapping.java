package nextstep.jwp.framework.infrastructure.mapping;

import java.lang.annotation.Annotation;
import java.util.Map;
import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.get.NotFoundRequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.get.StaticRequestAdapter;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;
import nextstep.jwp.framework.infrastructure.util.MethodAnnotationMapper;

public class HttpRequestMapping implements RequestMapping {

    private final Map<Key, RequestAdapter> adapters;

    public HttpRequestMapping(Map<Key, RequestAdapter> adapters) {
        this.adapters = adapters;
    }

    @Override
    public RequestAdapter findAdapter(HttpRequest httpRequest) {
        if (httpRequest.isRequestingStaticFiles()) {
            return new StaticRequestAdapter(StaticFileResolver.getInstance());
        }
        HttpMethod httpMethod = httpRequest.getMethod();
        Class<? extends Annotation> mappingAnnotation =
            MethodAnnotationMapper.findMappingClass(httpMethod);
        Key key = new Key(httpRequest.getUrl(), mappingAnnotation);
        return adapters.computeIfAbsent(key, then ->
            new NotFoundRequestAdapter(StaticFileResolver.getInstance()));
    }
}
