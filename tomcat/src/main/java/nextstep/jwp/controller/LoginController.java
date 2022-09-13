package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class LoginController extends AbstractController{

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (LoginHandler.isSession(request.getSession())) {
            response.redirect("/index.html");
            return;
        }
        response.ok("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        try {
            final User user = LoginHandler.login(request.getParams());
            response.redirect("/index.html");
            addSession(response, user);
        } catch (UserNotFoundException e) {
            response.unauthorized("/401.html");
        }
    }

    private void addSession(final HttpResponse response, final User user) {
        final Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);
        response.setSession(session);
    }
}
