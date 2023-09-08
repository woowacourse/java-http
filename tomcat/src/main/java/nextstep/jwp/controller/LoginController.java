package nextstep.jwp.controller;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.get(ACCOUNT);
        final String password = requestBody.get(PASSWORD);

        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
        if (!user.checkPassword(password)) {
            response.setHttpStatus(HttpStatus.UNAUTHORIZED).sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final String uuid = UUID.randomUUID().toString();
        response.setCookie("JSESSIONID", uuid);
        final Session session = new Session(uuid);
        session.setAttribute("user", user);
        sessionManager.add(session);
        response.setHttpStatus(HttpStatus.FOUND).sendRedirect(INDEX_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpCookie httpCookie = request.parseCookie();
        final Session session = sessionManager.findSession(httpCookie.getJSessionId());
        if (session != null) {
            response.setHttpStatus(HttpStatus.FOUND).sendRedirect(INDEX_PAGE);
            return;
        }
        response.setHttpStatus(HttpStatus.OK).sendRedirect(LOGIN_PAGE);
    }
}
