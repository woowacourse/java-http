package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.exception.UserNotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected String doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Session session = httpRequest.getSession();
        if (SessionManager.isValidateSession(session)) {
            return "redirect:/index";
        }
        return "login";
    }

    @Override
    protected String doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getValue("account");
        String password = requestBody.getValue("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        if (user.checkPassword(password)) {
            log.info("user : {}", user);
            issueSession(httpRequest, httpResponse, user);
        }

        return "redirect:/index";
    }

    private void issueSession(final HttpRequest httpRequest, final HttpResponse httpResponse, final User user) {
        Session session = httpRequest.getSession();
        session.setAttribute("user", user);
        httpResponse.setCookie(Cookie.ofJsession(session.getId()));
        SessionManager.add(session);
    }

    @Override
    public boolean isRest() {
        return false;
    }
}
