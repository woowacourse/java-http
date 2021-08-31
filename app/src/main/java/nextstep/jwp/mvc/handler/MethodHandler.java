package nextstep.jwp.mvc.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.mvc.annotation.RequestMapping;
import nextstep.jwp.mvc.argument.ArgumentResolverContainer;
import nextstep.jwp.mvc.exception.MethodHandlerException;
import nextstep.jwp.mvc.view.ModelAndView;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public abstract class MethodHandler implements Handler {

    private final Method method;
    private final Object target;
    private List<MethodParameter> methodParameters;
    private ArgumentResolverContainer argumentResolverContainer;

    public MethodHandler(Method method, Object target,
            ArgumentResolverContainer argumentResolverContainer) {
        this.method = method;
        this.target = target;
        this.methodParameters = methodParameters();
        this.argumentResolverContainer = argumentResolverContainer;
    }

    private List<MethodParameter> methodParameters() {
        List<MethodParameter> methodParameters = new ArrayList<>();
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            methodParameters.add(new MethodParameter(parameters[i], i));
        }
        return methodParameters;
    }

    ;

    @Override
    public boolean matchUrl(String httpUrl, HttpMethod httpMethod) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            final RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            return requestMapping.path().equals(httpUrl) &&
                    requestMapping.method().equals(httpMethod);
        }
        return false;
    }

    @Override
    public ModelAndView doRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        Object returnValue;
        try {
            returnValue = method.invoke(target, getMethodParameters(httpRequest, httpResponse));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MethodHandlerException();
        }
        return handleReturn(returnValue);
    }

    private Object[] getMethodParameters(HttpRequest httpRequest, HttpResponse httpResponse) {
        final Object[] parameters = new Object[methodParameters.size()];
        for (MethodParameter methodParameter : methodParameters) {
            final Object value = argumentResolverContainer.resolve(methodParameter, httpRequest, httpResponse);
            parameters[methodParameter.getParameterOrder()] = value;
        }
        return parameters;
    }

    protected abstract ModelAndView handleReturn(Object returnValue);
}
