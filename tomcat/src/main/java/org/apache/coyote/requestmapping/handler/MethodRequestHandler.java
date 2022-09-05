package org.apache.coyote.requestmapping.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.requestmapping.handler.Handler;

public class MethodRequestHandler implements Handler {

    private Method method;

    public MethodRequestHandler(final Method method) {
        this.method = method;
    }

    public HttpResponse handle(final HttpRequest httpRequest) {
        try {
            final Object controller = method.getDeclaringClass().getConstructor().newInstance();
            return (HttpResponse) method.invoke(controller, httpRequest);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException("해당 요청을 실행할 수 없습니다.");
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getTargetException();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("해당 컨트롤러를 생성할 수 없습니다.");
        }
    }
}
