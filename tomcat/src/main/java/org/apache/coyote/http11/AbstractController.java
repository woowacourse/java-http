package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class AbstractController implements Controller {

    private Map<String, MethodHandler> methodMapping = new HashMap<>();

    protected AbstractController() {
        methodMapping.put("GET", this::doGet);
        methodMapping.put("POST", this::doPost);
    }

    public HttpResponse service(HttpRequest request) {
        String method = request.getMethod().toUpperCase();
        MethodHandler handler = methodMapping.get(method);
        if (handler != null) {
            return handler.handle(request);
        }
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
                .responseBody("");
    }

    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
                .responseBody("");
    }

    protected HttpResponse doPost(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
                .responseBody("");
    }
}
