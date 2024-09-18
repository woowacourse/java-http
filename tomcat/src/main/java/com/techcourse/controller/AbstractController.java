package com.techcourse.controller;

import static com.techcourse.controller.RequestPath.METHOD_NOT_ALLOWED;

import java.util.Map;

import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, RequestHandling> requestHandling;

    protected AbstractController() {
        requestHandling = Map.of(
                HttpMethod.GET, this::doGet,
                HttpMethod.POST, this::doPost
        );
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = request.getMethod();
        RequestHandling handling = requestHandling.get(method);
        if (handling != null) {
            handling.handle(request, response);
            return;
        }
        sendNotAllowed(request, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        sendNotAllowed(request, response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        sendNotAllowed(request, response);
    }

    private void sendNotAllowed(HttpRequest request, HttpResponse response) {
        response.sendRedirect(METHOD_NOT_ALLOWED.path());
    }
}
