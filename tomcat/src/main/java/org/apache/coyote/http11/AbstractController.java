package org.apache.coyote.http11;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public non-sealed abstract class AbstractController implements Controller {

    protected static final Manager SESSION_MANAGER = new SessionManager();

    @Override
    public final void service(HttpRequest request, HttpResponse response) {
        if (!request.hasSessionCookie()) {
            Http11Cookie sessionCookie = Http11Cookie.sessionCookie();
            SESSION_MANAGER.add(new Session(sessionCookie.value()));
            response.addCookie(sessionCookie);
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
