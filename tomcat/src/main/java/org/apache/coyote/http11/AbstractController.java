package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class AbstractController implements Controller {

    private Map<String, MethodHandler> methodMapping = new HashMap<>();

    protected AbstractController() {
        methodMapping.put("GET", this::doGet);
        methodMapping.put("POST", this::doPost);
    }

    @Override
    public void service(HttpRequest request, HttpResponseNew response) {
        String method = request.getMethod().toUpperCase();
        MethodHandler handler = methodMapping.get(method);
        if (handler != null) {
            handler.handle(request, response);
        }
        if (handler == null) {
            handleMethodNotAllowed(response);
        }
    }

    private void handleMethodNotAllowed(HttpResponseNew response) {
        response.statusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
                .responseBody("");
    }

    protected void doGet(HttpRequest request, HttpResponseNew response) {
        handleMethodNotAllowed(response);
    }

    protected void doPost(HttpRequest request, HttpResponseNew response) {
        handleMethodNotAllowed(response);
    }
}
