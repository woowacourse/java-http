package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public final class RegisterController extends AbstractController {

    private final StaticResourceService resources = new StaticResourceService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        resources.serve("/register.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        boolean registered = InMemoryUserRepository.save(new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        ));

        if (!registered) {
            response.sendError(HttpStatus.CONFLICT, "Account already exists");
            return;
        }
        response.sendRedirect("/index.html");
    }
}
