package com.techcourse.controller;

import com.techcourse.infra.SessionManagerWrapper;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected static final SessionManagerWrapper SESSION_MANAGER = new SessionManagerWrapper(new SessionManager());

    @Override
    public final void service(HttpRequest request, HttpResponse response) {
        if (!hasSessionCookie(request)) {
            Http11Cookie sessionCookie = Http11Cookie.sessionCookie();
            SESSION_MANAGER.add(new Session(sessionCookie.value()));
            response.addCookie(sessionCookie);
        }

        if (hasSessionCookie(request) && sessionNotFound(request)) {
            SESSION_MANAGER.add(new Session(request.findSessionCookie().get().value()));
        }

        switch (getMethod(request)) {
            case GET -> doGet(request, response);
            case PUT -> doPut(request, response);
            case HEAD -> doHead(request, response);
            case POST -> doPost(request, response);
            case TRACE -> doTrace(request, response);
            case DELETE -> doDelete(request, response);
            case CONNECT -> doConnect(request, response);
            case OPTIONS -> doOptions(request, response);
            case null, default -> throw new RuntimeException("지원하지 않는 메서드 입니다.");
        }
    }

    private boolean hasSessionCookie(HttpRequest request) {
        return request.hasSessionCookie();
    }

    private boolean sessionNotFound(HttpRequest request) {
        return request.findSessionCookie().flatMap(SESSION_MANAGER::findBySessionCookie).isEmpty();
    }

    protected final boolean isLogin(HttpRequest request) {
        return request.findSessionCookie()
                .flatMap(SESSION_MANAGER::findBySessionCookie)
                .map(session -> session.hasAttribute("user"))
                .orElse(false);

    }

    protected final <T> void addSessionAttribute(HttpRequest request, String key, T value) {
        request.findSessionCookie().flatMap(SESSION_MANAGER::findBySessionCookie)
                .ifPresent(session -> session.setAttribute(key, value));
    }

    private Http11Method getMethod(HttpRequest request) {
        return request.method();
    }

    protected void doGet(HttpRequest request, HttpResponse response) {

    }

    protected void doPut(HttpRequest request, HttpResponse response) {

    }

    protected void doHead(HttpRequest request, HttpResponse response) {

    }

    protected void doPost(HttpRequest request, HttpResponse response) {

    }

    protected void doTrace(HttpRequest request, HttpResponse response) {

    }

    protected void doDelete(HttpRequest request, HttpResponse response) {

    }

    protected void doConnect(HttpRequest request, HttpResponse response) {

    }

    protected void doOptions(HttpRequest request, HttpResponse response) {

    }
}
