package nextstep.jwp.framework.infrastructure.adapter.post;

import java.lang.reflect.Method;
import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;

public abstract class HttpPostRequestAdapter implements RequestAdapter {

    protected final Class<?> target;
    protected final Method method;
    protected final StaticFileResolver staticFileResolver;

    protected HttpPostRequestAdapter(
        Class<?> target,
        Method method,
        StaticFileResolver staticFileResolver
    ) {
        this.target = target;
        this.method = method;
        this.staticFileResolver = staticFileResolver;
    }
}
