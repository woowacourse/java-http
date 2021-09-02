package nextstep.jwp.mvc.argument;

import nextstep.jwp.mvc.annotation.RequestParams;
import nextstep.jwp.mvc.handler.MethodParameter;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public class RequestParamsArgumentResolver implements HandlerMethodArgumentResolver{

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasAnnotationType(RequestParams.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpRequest httpRequest,
            HttpResponse httpResponse) {
        final RequestParams requestParams = parameter.getAnnotationOf(RequestParams.class);
        return httpRequest.getAttribute(requestParams.name());
    }
}
