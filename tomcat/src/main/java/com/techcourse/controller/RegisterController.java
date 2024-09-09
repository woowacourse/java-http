package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.controller.AbstractController;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.util.QueryStringParser;

public class RegisterController extends AbstractController {

    public RegisterController() {
        super.endPointPattern = Pattern.compile("^/register");
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        String body = request.getBody();
        Map<String, List<String>> queryStrings = QueryStringParser.parseQueryString(body);
        String account = queryStrings.get("account").getFirst();
        String email = queryStrings.get("email").getFirst();
        String password = queryStrings.get("password").getFirst();

        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        response.addStaticBody("/register.html");
    }
}
