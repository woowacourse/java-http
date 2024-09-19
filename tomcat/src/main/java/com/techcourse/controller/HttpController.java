package com.techcourse.controller;

import com.techcourse.exception.UnsupportedHttpMethodException;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import org.apache.coyote.http11.request.requestline.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class HttpController implements Controller {

    private final String path;

    protected HttpController(String path) {
        this.path = path;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (isSessionAlive(request)) {
            response.setHomeRedirection();
            return;
        }
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedHttpMethodException(request.getMethod().name());
    }

    private boolean isSessionAlive(HttpRequest request) {
        return SessionManager.validate(request.getCookieValue(Session.SESSION_KEY));
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalStateException("implement get method before invoke.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalStateException("implement post method before invoke.");
    }

    public boolean hasSamePath(String path) {
        return this.path.equals(path);
    }
}
