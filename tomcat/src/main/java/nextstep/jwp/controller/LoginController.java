package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionFactory;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.startline.Extension;
import org.apache.coyote.http11.request.startline.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class LoginController implements Controller {

    private static final String REDIRECT_PATH = "/index.html";
    private static final String KEY_SESSION = "JSESSIONID=";

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.isGetMethod()) {
            return doGet(request);
        }
        if (request.isPostMethod()) {
            return doPost(request);
        }

        throw new MethodNotAllowedException();
    }

    private HttpResponse doGet(HttpRequest request) {
        final Path path = request.getPath();
        final String responseBody = ResourceFindUtils
                .getResourceFile(path.getResource() + Extension.HTML.getExtension());

        if (request.containsSession()) {
            return toHttpResponseWithSession(request, path, responseBody);
        }

        return toHttpResponseWithCreatingSession(path, responseBody);
    }

    private HttpResponse toHttpResponseWithSession(HttpRequest request, Path path, String responseBody) {
        final SessionManager sessionManager = new SessionManager();
        final Session session = sessionManager.findSession(request.getSessionId());

        if (isLoggedIn(session)) {
            return new HttpResponse.Builder()
                    .status(HttpStatus.FOUND)
                    .location(REDIRECT_PATH)
                    .build();
        }
        return new HttpResponse.Builder()
                .status(HttpStatus.OK)
                .contentType(path.getContentType())
                .responseBody(responseBody)
                .build();
    }

    private boolean isLoggedIn(Session session) {
        return session != null && session.isLoggedInUserSession();
    }

    private HttpResponse toHttpResponseWithCreatingSession(Path path, String responseBody) {
        final Session session = SessionFactory.create();

        return new HttpResponse.Builder()
                .status(HttpStatus.OK)
                .cookie(KEY_SESSION + session.getId())
                .contentType(path.getContentType())
                .responseBody(responseBody)
                .build();
    }

    private HttpResponse doPost(HttpRequest request) {
        return login(request);
    }

    private HttpResponse login(HttpRequest request) {
        final Map<String, String> params = request.getBody();
        final String account = params.get("account");
        final String password = params.get("password");
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailedException::new);

        user.checkPassword(password);
        if (request.containsSession()) {
            return generateRedirectResponse(request, user);
        }
        return generateRedirectResponseWithNewSession(user);
    }

    private HttpResponse generateRedirectResponse(HttpRequest request, User user) {
        final String sessionId = request.getSessionId();
        setUserToSession(sessionId, user);
        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .location(REDIRECT_PATH)
                .build();
    }

    private HttpResponse generateRedirectResponseWithNewSession(User user) {
        final Session session = SessionFactory.create();
        final SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);
        session.setAttribute("user", user);
        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .cookie(KEY_SESSION + session.getId())
                .location(REDIRECT_PATH)
                .build();
    }

    private void setUserToSession(String sessionId, User user) {
        final SessionManager sessionManager = new SessionManager();
        final Session session = sessionManager.findSession(sessionId);

        if (session == null) {
            final Session newSession = new Session(sessionId);
            sessionManager.add(newSession);
            newSession.setAttribute("user", user);
            return;
        }
        session.setAttribute("user", user);
    }
}
