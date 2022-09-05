package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.UserLoginRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.SimpleHttpResponse;
import org.apache.coyote.web.session.Cookie;
import org.apache.coyote.web.session.Session;
import org.apache.coyote.web.session.SessionManager;

public class UserLoginController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final SimpleHttpResponse httpResponse) {
        UserLoginRequest request = UserLoginRequest.from(httpRequest.getParameters());
        try {
            User user = InMemoryUserRepository.findByAccount(request.getAccount())
                    .orElseThrow(UserNotFoundException::new);
            if (user.checkPassword(request.getPassword())) {
                addCookie(httpResponse, user);
                httpResponse.redirect("/index.html");
            }
        } catch (Exception e) {
            throw e;
            // TODO : 401, 404
        }
    }

    private void addCookie(final SimpleHttpResponse httpResponse, final User user) {
        Cookie cookie = SessionManager.createCookie();
        Session session = new Session(cookie.getKey());
        session.setAttribute("user", user);
        SessionManager.addSession(cookie.getValue(), session);
        httpResponse.addCookie(cookie);
    }
}
