package org.apache.coyote.handle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.coyote.handle.handler.Handler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HandlerMethod {

    private final Handler handler;
    private final Method method;

    public HandlerMethod(final Handler handler, final Method method) {
        this.handler = handler;
        this.method = method;
    }

    public void invokeMethod(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws InvocationTargetException, IllegalAccessException {
        method.invoke(handler, httpRequest, httpResponse);
    }
}
