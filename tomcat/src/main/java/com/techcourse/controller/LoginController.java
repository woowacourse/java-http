package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.IncorrectPasswordException;
import com.techcourse.exception.UnknownAccountException;
import com.techcourse.model.User;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends HttpController {
    public LoginController(String path) {
        super(path);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        ResourceFinder.setStaticResponse(request, response);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getPayload().get("account");
        String password = request.getPayload().get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnknownAccountException(account));

        validateUserPassword(user, password);

        Session session = createSession(user);

        response.setCookie(Session.SESSION_KEY, session.getId());
    }

    private void validateUserPassword(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new IncorrectPasswordException();
        }
    }

    private Session createSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);
        return session;
    }
}
