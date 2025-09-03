package com.java.servlet;

import java.util.List;

public class Servlets {

    private static final List<Servlet> servlets = null;

    public static Servlet findServletFor(HttpRequest request) {
        // TODO : 명확한 예외 타입 사용
        return servlets.stream()
                .filter(servlet -> servlet.canHandle(request))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
