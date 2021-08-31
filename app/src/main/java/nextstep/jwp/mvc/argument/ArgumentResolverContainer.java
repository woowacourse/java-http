package nextstep.jwp.mvc.argument;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.core.mvc.argumentresolver.ArgumentResolver;
import nextstep.jwp.mvc.handler.MethodParameter;
import nextstep.jwp.webserver.request.HttpRequest;

public class ArgumentResolverContainer {

    private List<HandlerMethodArgumentResolver> argumentResolvers;

    public ArgumentResolverContainer() {
        this.argumentResolvers = defaultArgumentResolvers();
    }

    private List<HandlerMethodArgumentResolver> defaultArgumentResolvers() {
        List<HandlerMethodArgumentResolver> argumentResolvers =
                new ArrayList<>();
        argumentResolvers.add(new ModelAttributeArgumentResolver());
        return argumentResolvers;
    }

    public Object resolve(MethodParameter methodParameter, HttpRequest httpRequest) {
        for (HandlerMethodArgumentResolver argumentResolver : argumentResolvers) {
            if(argumentResolver.supportsParameter(methodParameter)) {
                return argumentResolver.resolveArgument(methodParameter, httpRequest);
            }
        }
        throw new IllegalStateException("can not resolve method parameter");
    }
}
