package org.apache.catalina.handler;

import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RequestMappingHandler {

    private static final RequestMappingHandler INSTANCE = new RequestMappingHandler();

    public static RequestMappingHandler getInstance() {
        return INSTANCE;
    }

    public HttpResponse request(HttpRequest request) {
        HttpResponse response = new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/404.html");

        if (request.getPath().equals("/")) {
            response = new HttpResponse(HttpStatusCode.OK, ContentType.HTML, request.getPath());
        }
        if (request.getPath().endsWith(".html")) {
            response = new HttpResponse(HttpStatusCode.OK, ContentType.HTML, request.getPath());
        }
        if (request.getPath().endsWith(".css")) {
            response = new HttpResponse(HttpStatusCode.OK, ContentType.CSS, request.getPath());
        }
        if (request.getPath().endsWith(".js")) {
            response = new HttpResponse(HttpStatusCode.OK, ContentType.JAVASCRIPT, request.getPath());
        }
        if (request.getPath().equals("/login")) {
            response = LoginHandler.getInstance().handle(request);
        }
        if (request.getPath().equals("/register")) {
            response = RegisterHandler.getInstance().handle(request);
        }

        setSession(request, response);
        return response;
    }

    private void setSession(HttpRequest request, HttpResponse response) {
        if (request.getCookie("JSESSIONID") == null) {
            Session session = new Session(UUID.randomUUID().toString());
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.add(session);
            response.addCookie("JSESSIONID", session.getId());
        }
    }
}
