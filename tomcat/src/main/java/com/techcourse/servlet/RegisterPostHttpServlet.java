package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParameters;

public class RegisterPostHttpServlet implements HttpServlet {

    @Override
    public boolean canService(HttpRequest request) {
        return request.hasMethod(HttpMethod.POST) && request.hasPath("/register");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String content = request.getContent();
        QueryParameters queryParameters = new QueryParameters(content);
        User user = new User(
                queryParameters.get("account"), queryParameters.get("password"), queryParameters.get("email")
        );
        InMemoryUserRepository.save(user);
        response.redirectTo("/index.html");
    }
}
