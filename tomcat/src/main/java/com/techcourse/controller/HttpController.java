package com.techcourse.controller;

import com.techcourse.exception.UnsupportedHttpMethodException;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import java.util.stream.Stream;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.requestline.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class HttpController implements Controller {

    private final String path;

    protected HttpController(String path) {
        this.path = path;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        doPreIntercept(request, response);
        doService(request, response);
        doPostIntercept(request, response);
    }

    private void doPreIntercept(HttpRequest request, HttpResponse response) {
        if (request.getMethod() == HttpMethod.GET && isSessionAlive(request)) {
            Stream.of("/login", "/register")
                    .filter(this::hasSamePath)
                    .findAny()
                    .ifPresent(x -> response.setHomeRedirection());
        }
    }

    private void doPostIntercept(HttpRequest request, HttpResponse response) {
        if (request.getMethod() == HttpMethod.POST) {
            Stream.of("/login", "/register")
                    .filter(this::hasSamePath)
                    .findAny()
                    .ifPresent(x -> response.setRedirect("/greeting"));
        }
    }

    private void doService(HttpRequest request, HttpResponse response) throws Exception {
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

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalStateException("implement get method before invoke.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalStateException("implement post method before invoke.");
    }

    private boolean isSessionAlive(HttpRequest request) {
        return SessionManager.isRegisteredId(request.getCookieValue(Session.SESSION_KEY));
    }

    public String getPath() {
        return path;
    }

    public boolean hasSamePath(String path) {
        return getPath().equals(path);
    }
}
