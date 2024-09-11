package com.techcourse.handler;

import com.techcourse.model.User;
import hoony.was.RequestHandler;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginGetRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.hasMethod(HttpMethod.GET) && request.hasPath("/login");
    }

    @Override
    public String handle(HttpRequest request, HttpResponse response) {
        Session session = request.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "login.html";
        }
        return "redirect:/index.html";
    }
}
