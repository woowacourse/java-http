package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import hoony.was.RequestHandler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.QueryParameters;

public class RegisterPostRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.hasMethod(HttpMethod.POST) && request.hasPath("/register");
    }

    @Override
    public String handle(HttpRequest request) {
        String content = request.getContent();
        QueryParameters queryParameters = new QueryParameters(content);
        User user = new User(
                queryParameters.get("account"), queryParameters.get("password"), queryParameters.get("email")
        );
        InMemoryUserRepository.save(user);
        return "redirect:/index.html";
    }
}
