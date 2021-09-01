package nextstep.jwp;

import nextstep.jwp.infrastructure.http.interceptor.HandlerInterceptor;
import nextstep.jwp.infrastructure.http.interceptor.InterceptorResolver;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public class MockInterceptorResolver implements HandlerInterceptor {

    public MockInterceptorResolver() {
    }

    @Override
    public void preHandle(final HttpRequest request, final HttpResponse response) throws Exception {

    }

    @Override
    public void postHandle(final HttpRequest request, final HttpResponse response) throws Exception {

    }
}
