package nextstep.jwp.mvc.argument;

import nextstep.jwp.mvc.handler.MethodParameter;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(MethodParameter parameter);

    Object resolveArgument(MethodParameter parameter, HttpRequest httpRequest, HttpResponse httpResponse);
}
