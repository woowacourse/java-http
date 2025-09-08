package org.apache.catalina.handler;

import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RequestMappingHandler {

    public HttpResponse request(HttpRequest request) {
        if (request.getPath().equals("/")) {
            HttpResponse response = new HttpResponse(HttpStatusCode.OK, ContentType.HTML, request.getPath());
            setSession(request, response);
            return response;
        }
        if (request.getPath().endsWith(".html")) {
            HttpResponse response = new HttpResponse(HttpStatusCode.OK, ContentType.HTML, request.getPath());
            setSession(request, response);
            return response;
        }
        if (request.getPath().endsWith(".css")) {
            HttpResponse response = new HttpResponse(HttpStatusCode.OK, ContentType.CSS, request.getPath());
            setSession(request, response);
            return response;
        }
        if (request.getPath().endsWith(".js")) {
            HttpResponse response = new HttpResponse(HttpStatusCode.OK, ContentType.JAVASCRIPT, request.getPath());
            setSession(request, response);
            return response;
        }
        if (request.getPath().equals("/login")) {
            LoginHandler loginHandler = new LoginHandler();
            HttpResponse response = loginHandler.handle(request);
            setSession(request, response);
            return response;
        }
        if (request.getPath().equals("/register")) {
            RegisterHandler registerHandler = new RegisterHandler();
            HttpResponse response = registerHandler.handle(request);
            setSession(request, response);
            return response;
        }

        return new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/404.html");
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
