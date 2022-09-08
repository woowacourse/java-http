package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.EmptyParameterException;
import nextstep.jwp.exception.PasswordNotMatchException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import nextstep.jwp.presentation.dto.UserLoginRequest;
import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpCookie;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.support.session.Session;
import org.apache.coyote.http11.support.session.SessionManager;
import org.apache.coyote.http11.web.QueryParameters;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Objects;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws IOException {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        return new HttpResponse(HttpStatus.OK, httpHeaders, ResourceLoader.getContent("login.html"));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final String requestBody = httpRequest.getRequestBody();
        final QueryParameters queryParameters = QueryParameters.from(requestBody);
        final UserLoginRequest userLoginRequest = UserLoginRequest.from(queryParameters);

        try {
            validateParametersExist(userLoginRequest);
            final String password = userLoginRequest.getPassword();
            final User user = findUser(userLoginRequest.getAccount());
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

    private void validateParametersExist(final UserLoginRequest userLoginRequest) {
        if (Objects.isNull(userLoginRequest.getAccount()) ||
                Objects.isNull(userLoginRequest.getPassword())) {
            throw new EmptyParameterException();
        }
    }

    private User findUser(final String account) {
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
