package org.apache.catalina.servlet;

import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;

public abstract class AbstractServlet implements Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        switch (request.getRequestMethod()) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }

    protected boolean possibleHandle(String requestPath) {
        /* NOOP */
        return false;
    }
}
