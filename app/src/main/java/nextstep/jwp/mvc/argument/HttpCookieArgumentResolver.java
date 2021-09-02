package nextstep.jwp.mvc.argument;

import nextstep.jwp.mvc.handler.MethodParameter;
import nextstep.jwp.webserver.request.HttpCookie;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public class HttpCookieArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.isTypeOf(HttpCookie.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpRequest httpRequest,
            HttpResponse httpResponse) {
        return httpRequest.getCookie();
    }
}
