package nextstep.jwp.core.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import nextstep.jwp.core.handler.mapping.RequestMapping;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public class MethodHandler extends ResolverHandler {

    private static final String REDIRECT_FORM = "redirect:";

    private final Method method;
    private final Object target;
    private final String url;
    private final HttpMethod httpMethod;
//    private final List<ArgumentResolver> argumentResolvers;

    public MethodHandler(Method method, Object target, String url, HttpMethod httpMethod) {
        this.method = method;
        this.target = target;
        this.url = url;
        this.httpMethod = httpMethod;
//        this.argumentResolvers = argumentResolvers;
    }

    @Override
    public boolean matchUrl(String httpUrl, HttpMethod httpMethod) {
        if(method.isAnnotationPresent(RequestMapping.class)) {

            final RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            return requestMapping.path().equals(httpUrl) &&
                    requestMapping.method().equals(httpMethod);
        }
        return false;
    }

    @Override
    protected ModelAndView doRequestWithResolving(HttpRequest httpRequest,
            HttpResponse httpResponse) {
        // TODO : RestController 와 Controller 구분하기
//        Object[] parameters = getParameters(method.getParameters());

        try {
            final String viewName = (String) method.invoke(target, httpRequest, httpResponse);

//          TODO : ViewResolver 로 뷰 관련 다루기(일단은 ModelAndView에서)
            if(viewName.startsWith(REDIRECT_FORM)) {
                final String redirectUrl = viewName.substring(REDIRECT_FORM.length());
                httpResponse.addRedirectUrl(redirectUrl);
                return new ModelAndView(redirectUrl);
            }
            return new ModelAndView(viewName, httpRequest.httpUrl());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

//    TODO : ArgumentResolver 완성하기
//    private Object[] getParameters(Parameter[] parameters) {
//        final Object[] objects = new Object[parameters.length];
//        for (int i = 0; i < parameters.length; i++) {
//
//        }
//        return new Object[0];
//    }
}
