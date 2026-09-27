package com.techcourse.controller;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.controller.StaticResourceController;

public class LoginController extends AbstractController {

    private final StaticResourceController staticResource;

    public LoginController(StaticResourceController staticResource) {
        this.staticResource = staticResource;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        User loggedInUser = (User) request.getSession().getAttribute("user");

        if (loggedInUser != null) {
            response.sendRedirect("/index.html");
            return;
        }

        staticResource.serve("/login.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        if (isAnyBlank(account, password)) {
            response.sendRedirect("/401.html");
            return;
        }

        Optional<User> authenticateUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (authenticateUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        request.getSession().setAttribute("user", authenticateUser.get());
        response.sendRedirect("/index.html");
    }
}
