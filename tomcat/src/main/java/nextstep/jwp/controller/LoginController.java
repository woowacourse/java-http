package nextstep.jwp.controller;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.Extension;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class LoginController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.isGetMethod()) {
            return doGet(request);
        }

        if (request.isPostMethod()) {
            return doPost(request);
        }

        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }

    private HttpResponse doGet(HttpRequest request) {
        final Path path = request.getPath();
        final String responseBody = ResourceFindUtils
                .getResourceFile(path.getResource() + Extension.HTML.getExtension());

        if (request.containsSession()) {
            final SessionManager sessionManager = new SessionManager();
            final Session session = sessionManager.findSession(request.getSessionId());

            if (session != null) {
                return new HttpResponse.Builder()
                        .status(HttpStatus.FOUND)
                        .location("/index.html")
                        .build();
            }
            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .contentType(path.getContentType())
                    .responseBody(responseBody)
                    .build();

        }

        final Session session = new Session(UUID.randomUUID().toString());
        final SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);

        return new HttpResponse.Builder()
                .status(HttpStatus.OK)
                .cookie("JSESSIONID=" + session.getId())
                .contentType(path.getContentType())
                .responseBody(responseBody)
                .build();
    }

    private HttpResponse doPost(HttpRequest request) {
        login(request.getBody(), request.getSessionId());

        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .location("/index.html")
                .build();
    }

    private void login(Map<String, String> params, String sessionId) {
        final String account = params.get("account");
        final String password = params.get("password");
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailedException::new);

        user.checkPassword(password);
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
