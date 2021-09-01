package nextstep.jwp.infrastructure.http.interceptor;

import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public interface HandlerInterceptor {

    void preHandle(final HttpRequest request, final HttpResponse response) throws Exception;
    void postHandle(final HttpRequest request, final HttpResponse response) throws Exception;
}
