package org.apache.catalina.controller;

import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {
    private final Map<String, Controller> handlers = Map.of(
            "GET", this::doGet,
            "POST", this::doPost
    );

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Controller handler = handlers.get(request.getMethod());
        if (handler == null) {
            return;
        }
        handler.service(request, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}
