package nextstep.jwp.mvc.argument;

import nextstep.jwp.mvc.annotation.ModelAttribute;
import nextstep.jwp.mvc.handler.MethodParameter;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public class ModelAttributeArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasAnnotationType(ModelAttribute.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpRequest httpRequest, HttpResponse httpResponse) {
        return parameter.createInstance(httpRequest.getRequestParams());
    }
}
