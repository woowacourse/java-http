package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.notContainJsessionId()) {
            redirectToLoginPage(response);
            return;
        }
        final String jsessionId = request.findJsessionId();
        final Session session = sessionManager.findSession(jsessionId);
        if (session == null) {
            redirectToLoginPage(response);
            return;
        }
        response.setStatusCode(StatusCode.FOUND)
                .setContentType(ContentType.HTML)
                .setRedirect("/index.html");
    }

    private void redirectToLoginPage(final HttpResponse response) {
        response.setStatusCode(StatusCode.OK)
                .setContentType(ContentType.HTML)
                .setRedirect("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        if (request.containJsessionId()) {
            generateAlreadyAuthorized(request.findJsessionId(), response);
            return;
        }
        final User user = findUser(request);
        if (user == null) {
            generateUnauthorized(response);
            return;
        }
        final Session session = createSession(user);
        generateLogin(response, session);
    }

    private void generateAlreadyAuthorized(final String jsessionId, final HttpResponse response) {
        final Session session = sessionManager.findSession(jsessionId);
        if (session == null) {
            response.setStatusCode(StatusCode.OK)
                    .setContentType(ContentType.HTML)
                    .setRedirect("/register.html");
            return;
        }
        generateLogin(response, session);
    }

    private void generateLogin(final HttpResponse response, final Session session) {
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        response.setStatusCode(StatusCode.OK)
                .setContentType(ContentType.HTML)
                .setRedirect("/index.html");
    }

    private User findUser(final HttpRequest request) {
        final String account = request.findBodyParameter("account");
        final String password = request.findBodyParameter("password");
        return checkUser(account, password);
    }

    private void generateUnauthorized(final HttpResponse response) {
        response.setStatusCode(StatusCode.UNAUTHORIZED)
                .setContentType(ContentType.HTML)
                .setRedirect("/401.html");
    }

    private User checkUser(final String account, final String password) {
        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            return optionalUser.get();
        }
        return null;
    }


    private Session createSession(final User user) {
        final Session session = new Session(String.valueOf(UUID.randomUUID()));
        session.addAttribute("user", user);
        sessionManager.add(session);
        return session;
    }
}
