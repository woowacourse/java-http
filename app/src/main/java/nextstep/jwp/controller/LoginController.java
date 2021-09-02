package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DBNotFoundException;
import nextstep.jwp.exception.PasswordMismatchException;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;
import nextstep.jwp.framework.http.session.HttpSession;
import nextstep.jwp.framework.http.session.HttpSessions;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        checkAlreadyLogin(request);
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), HttpStatus.OK);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final HttpStatus status = login(request);
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), status);
    }

    private HttpStatus login(final HttpRequest request) {
        try {
            return checkAccount(request);
        } catch (DBNotFoundException | PasswordMismatchException ignore) {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    private void checkAlreadyLogin(final HttpRequest request) {
        if (HttpSessions.isLoggedIn(request.sessionId())) {
            request.changeIndexPage();
        }
    }

    private HttpStatus checkAccount(final HttpRequest request) {
        final Map<String, String> queryParams = request.getQueryParams();
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow(DBNotFoundException::new);

        if (user.checkPassword(password)) {
            logger.debug(account + "님이 접속했습니다.");
            final HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", user);
            request.cookie(httpSession.getId());
            return HttpStatus.FOUND;
        }
        throw new PasswordMismatchException();
    }
}
