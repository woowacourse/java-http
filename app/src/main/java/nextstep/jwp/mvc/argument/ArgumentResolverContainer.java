package nextstep.jwp.mvc.argument;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.mvc.handler.MethodParameter;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public class ArgumentResolverContainer {

    private List<HandlerMethodArgumentResolver> argumentResolvers;

    public ArgumentResolverContainer() {
        this.argumentResolvers = defaultArgumentResolvers();
    }

    private List<HandlerMethodArgumentResolver> defaultArgumentResolvers() {
        List<HandlerMethodArgumentResolver> argumentResolvers =
                new ArrayList<>();
        argumentResolvers.add(new HttpRequestArgumentResolver());
        argumentResolvers.add(new HttpResponseArgumentResolver());
        argumentResolvers.add(new ModelAttributeArgumentResolver());
        argumentResolvers.add(new RequestParamsArgumentResolver());
        argumentResolvers.add(new HttpCookieArgumentResolver());
        argumentResolvers.add(new HttpSessionArgumentResolver());
        return argumentResolvers;
    }

    public Object resolve(MethodParameter methodParameter, HttpRequest httpRequest, HttpResponse httpResponse) {
        for (HandlerMethodArgumentResolver argumentResolver : argumentResolvers) {
            if(argumentResolver.supportsParameter(methodParameter)) {
                return argumentResolver.resolveArgument(methodParameter, httpRequest, httpResponse);
            }
        }
        throw new IllegalStateException("can not resolve method parameter");
    }
}
