package com.http.application;

import com.http.application.servlet.RequestServlet;
import com.http.application.servlet.impl.LoginRequestServlet;
import com.http.domain.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public final class RequestServletContainer {

    private static final Map<String, RequestServlet> handlers = new HashMap<>();

    private RequestServletContainer() {
    }

    static {
        handlers.put("/login", new LoginRequestServlet());
    }

    public static void handle(HttpRequest httpRequest) {
        final String path = httpRequest.startLine().path();

        if (handlers.containsKey(path)) {
            handlers.get(path).handle(httpRequest);
        }
    }
}
