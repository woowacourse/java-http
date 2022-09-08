package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.UserLoginRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.support.Url;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.HttpResponse;
import org.apache.coyote.web.session.Cookie;
import org.apache.coyote.web.session.Session;
import org.apache.coyote.web.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginController extends AbstractController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserLoginController.class);

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        UserLoginRequest request = UserLoginRequest.from(httpRequest.getParameters());
        try {
            User user = InMemoryUserRepository.findByAccount(request.getAccount())
                    .orElseThrow(UserNotFoundException::new);
            if (user.checkPassword(request.getPassword())) {
                addCookie(httpResponse, user);
                httpResponse.redirect(Url.createUrl("/index.html"));
                return;
            }
            httpResponse.sendError(HttpStatus.UNAUTHORIZED, Url.createUrl("/401.html"));
        } catch (UserNotFoundException e) {
            LOGGER.error("error", e);
            httpResponse.sendError(HttpStatus.BAD_REQUEST, Url.createUrl("/400.html"));
        } catch (Exception e) {
            LOGGER.error("error", e);
            httpResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR, Url.createUrl("/500.html"));
        }
    }

    private void addCookie(final HttpResponse httpResponse, final User user) {
        Cookie cookie = SessionManager.createCookie();
        Session session = new Session(cookie.getKey());
        session.setAttribute("user", user);
        SessionManager.addSession(cookie.getValue(), session);
        httpResponse.addCookie(cookie);
    }
}
