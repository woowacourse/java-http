package org.apache.catalina.controller;

import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
