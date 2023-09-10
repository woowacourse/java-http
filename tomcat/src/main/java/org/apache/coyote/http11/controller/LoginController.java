package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public class LoginController extends AbstractController {
    @Override
    protected void doPost(final Request request, final Response response) {
        final Map<String, String> requestForms = request.getRequestForms().getFormData();
        final Optional<User> user = login(requestForms.get("account"), requestForms.get("password"));
        if (user.isPresent()) {
            loginSuccess(request, response, user.get());
            return;
        }
        loginFail(response);
    }

    private void loginSuccess(final Request request, final Response response, final User user) {
        response.location("index.html");
        response.status(HttpStatus.FOUND);

        if (request.noSession()) {
            final Session session = new Session();
            session.setAttribute("user", user);
            SessionManager.add(session);
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        }
    }

    private void loginFail(final Response response) {
        final Response failedResponse = Response.createByTemplate(HttpStatus.UNAUTHORIZED, "401.html");
        response.setBy(failedResponse);
    }

    private Optional<User> login(final String account, final String password) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        if (request.getSessionValue("user") != Optional.empty()) {
            response.location("index.html");
            response.status(HttpStatus.FOUND);
            return;
        }
        final Response createdResponse = Response.createByTemplate(HttpStatus.OK, "login.html");
        response.setBy(createdResponse);
    }
}
