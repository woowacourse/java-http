package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public HttpResponse request(HttpRequest request) {
        HttpResponse response = new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/404.html");

        if (request.isStaticResource()) {
            response = StaticResourceController.getInstance().service(request);
        }
        if (request.getPath().equals("/")) {
            response = HomeController.getInstance().service(request);
        }
        if (request.getPath().equals("/login")) {
            response = LoginController.getInstance().service(request);
        }
        if (request.getPath().equals("/register")) {
            response = RegisterController.getInstance().service(request);
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
