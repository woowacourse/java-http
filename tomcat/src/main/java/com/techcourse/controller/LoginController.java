package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;

public class LoginController extends AbstractController {
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setResource("login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        if (request.existSessionAttribute("user")) {
            setRedirectIndex(response);
            return;
        }
        final String account = request.getBodyAttribute("account");
        final String password = request.getBodyAttribute("password");

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> login(request, response, user),
                        () -> setRedirectFail(response)
                );
    }


    private void login(final HttpRequest request, final HttpResponse response, final User user) {
        final Session session = request.getSession();
        session.setAttribute("user", user);
        response.addCookie(Cookies.SESSION_ID, session.getId());
        setRedirectIndex(response);
    }

    private void setRedirectFail(final HttpResponse response) {
        response.setRedirect("401.html");
    }

    private void setRedirectIndex(final HttpResponse response) {
        response.setRedirect("index.html");
    }
}
