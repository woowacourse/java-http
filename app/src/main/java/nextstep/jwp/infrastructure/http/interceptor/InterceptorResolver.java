package nextstep.jwp.infrastructure.http.interceptor;

import java.util.Set;
import nextstep.jwp.infrastructure.http.ClassScanner;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public class InterceptorResolver implements HandlerInterceptor {

    private final Set<HandlerInterceptor> interceptors;

    public InterceptorResolver(final String interceptorPackage) {
        this.interceptors = findAllInterceptors(interceptorPackage);
    }

    private static Set<HandlerInterceptor> findAllInterceptors(final String interceptorPackage) {
        final ClassScanner<HandlerInterceptor> classScanner = new ClassScanner<>(interceptorPackage);
        return classScanner.scanAs(HandlerInterceptor.class);
    }

    @Override
    public void preHandle(final HttpRequest request, final HttpResponse response) throws Exception {
        for (HandlerInterceptor interceptor : interceptors) {
            interceptor.preHandle(request, response);
        }
    }

    @Override
    public void postHandle(final HttpRequest request, final HttpResponse response) throws Exception {
        for (HandlerInterceptor interceptor : interceptors) {
            interceptor.postHandle(request, response);
        }
    }
}
