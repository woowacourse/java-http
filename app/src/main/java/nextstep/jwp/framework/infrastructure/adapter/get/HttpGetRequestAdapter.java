package nextstep.jwp.framework.infrastructure.adapter.get;

import java.lang.reflect.Method;
import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;

public abstract class HttpGetRequestAdapter implements RequestAdapter {

    protected final Class<?> target;
    protected final Method method;
    protected final StaticFileResolver staticFileResolver;

    protected HttpGetRequestAdapter(
        Class<?> target,
        Method method,
        StaticFileResolver staticFileResolver
    ) {
        this.target = target;
        this.method = method;
        this.staticFileResolver = staticFileResolver;
    }
}
