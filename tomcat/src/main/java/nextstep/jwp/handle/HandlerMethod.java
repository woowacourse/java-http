package nextstep.jwp.handle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HandlerMethod {

    private final Controller controller;
    private final Method method;

    public HandlerMethod(final Controller controller, final Method method) {
        this.controller = controller;
        this.method = method;
    }

    public void invokeMethod(final HttpRequest request, final HttpResponse response)
            throws InvocationTargetException, IllegalAccessException {
        method.invoke(controller, request, response);
    }
}
