package com.techcourse.servlet;

import com.techcourse.model.User;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginServlet implements HttpServlet {

    @Override
    public boolean canService(HttpRequest request) {
        return request.hasMethod(HttpMethod.GET) && request.hasPath("/login");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Session session = request.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.redirectTo("/login.html");
            return;
        }
        response.redirectTo("/index.html");
    }
}
