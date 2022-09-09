package nextstep.jwp.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, Method> methods = new HashMap<>();

    public AbstractController() {
        try {
            methods.put(HttpMethod.GET, getClass().getDeclaredMethod("doGet", HttpRequest.class, HttpResponse.class));
            methods.put(HttpMethod.POST, getClass().getDeclaredMethod("doPost", HttpRequest.class, HttpResponse.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Method method = methods.get(httpRequest.getMethod());
        method.invoke(this, httpRequest, httpResponse);
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {

    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }
}
