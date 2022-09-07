package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.EmptyParameterException;
import nextstep.jwp.exception.PasswordNotMatchException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.support.HttpCookie;
import org.apache.coyote.http11.support.session.Session;
import org.apache.coyote.http11.support.session.SessionManager;
import org.apache.coyote.http11.web.QueryParameters;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public HttpResponse login(final QueryParameters queryParameters) {
        try {
            validateQueryParametersExist(queryParameters);
            final String account = queryParameters.getValueByKey("account");
            final String password = queryParameters.getValueByKey("password");
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(UserNotFoundException::new);
            log.info("user: {}", user);
            validatePassword(user, password);

            final HttpCookie httpCookie = HttpCookie.create();
            addSession(user, httpCookie);

            return HttpResponse.sendRedirectWithCookie("/index.html", httpCookie);

        } catch (EmptyParameterException e) {
            return HttpResponse.sendRedirect("/login.html");
        } catch (UserNotFoundException | PasswordNotMatchException e) {
            return HttpResponse.sendRedirect("/401.html");
        }
    }

    private void validateQueryParametersExist(final QueryParameters queryParameters) {
        if (queryParameters.isEmpty()) {
            throw new EmptyParameterException();
        }
    }

    private void validatePassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new PasswordNotMatchException();
        }
    }

    private void addSession(final User user, final HttpCookie httpCookie) {
        final Session session = new Session(httpCookie.getJSessionId());
        final SessionManager sessionManager = SessionManager.getInstance();
        session.setAttribute("user", user);
        sessionManager.add(session);
    }
}
