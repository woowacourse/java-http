package org.apache.catalina.servlet;

import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;

public class AbstractHttpServlet implements HttpServlet {

    private final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> handlers = Map.of(
            HttpMethod.GET, this::doGet,
            HttpMethod.POST, this::doPost
    );

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();
        BiConsumer<HttpRequest, HttpResponse> httpHandler = handlers.get(method);
        if (httpHandler == null) {
            response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
            return;
        }
        httpHandler.accept(request, response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.METHOD_NOT_ALLOWED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.METHOD_NOT_ALLOWED);
    }
}
