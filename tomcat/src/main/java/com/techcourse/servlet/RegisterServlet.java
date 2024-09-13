package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.servlet.AbstractHttpServlet;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParameters;
import org.apache.coyote.http11.StaticResourceServer;

public class RegisterServlet extends AbstractHttpServlet {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        StaticResourceServer.load(response, "/register.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String content = request.getContent();
        QueryParameters queryParameters = new QueryParameters(content);
        User user = new User(
                queryParameters.get("account"), queryParameters.get("password"), queryParameters.get("email")
        );
        InMemoryUserRepository.save(user);
        response.redirectTo("/index.html");
    }
}
