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
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        final QueryParameters queryParameters = httpRequest.getQueryParameters();

        try {
            validateQueryParametersExist(queryParameters);
            final String password = queryParameters.getValueByKey("password");
            final User user = findUser(queryParameters);
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

    private User findUser(final QueryParameters queryParameters) {
        final String account = queryParameters.getValueByKey("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
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
