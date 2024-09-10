package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.controller.AbstractController;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.util.QueryStringParser;

public class LoginController extends AbstractController {

    public LoginController() {
        super("/login");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getBody();
        Map<String, List<String>> queryStrings = QueryStringParser.parseQueryString(body);
        String account = queryStrings.get("account").getFirst();
        String password = queryStrings.get("password").getFirst();

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                Session session = request.getSession();
                session.setAttribute("user", user);
                response.addCookie("JSESSIONID", session.getId());
                response.sendRedirect("/index.html");
                return;
            }
        }
        response.sendRedirect("/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (hasUser(request)) {
            response.sendRedirect("/index.html");
            return;
        }
        response.addStaticBody("/login.html");
    }

    private boolean hasUser(HttpRequest request) {
        try {
            Session session = request.getSession();
            session.getAttribute("user");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
