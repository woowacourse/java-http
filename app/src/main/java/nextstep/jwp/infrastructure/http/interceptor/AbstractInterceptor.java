package nextstep.jwp.infrastructure.http.interceptor;

import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public abstract class AbstractInterceptor implements HandlerInterceptor {

    @Override
    public void preHandle(final HttpRequest request, final HttpResponse response) {

    }

    @Override
    public void postHandle(final HttpRequest request, final HttpResponse response) {

    }
}
