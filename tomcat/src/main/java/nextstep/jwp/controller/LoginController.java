package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpCookie cookie = request.getCookie();
        final User user = findUserBySessionId(cookie.getJSessionId());
        final var statusLine = StatusLine.of(request.getRequestLine().getProtocol(), HttpStatus.FOUND);
        response.setStatusLine(statusLine);
        if (user == null) {
            handleFirstLogin(request, response);
            return;
        }
        response.addResponseHeader(HEADER_LOCATION, INDEX_PAGE);
    }

    private void handleFirstLogin(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> requestBodyValues = request.getRequestParameters();
        final Optional<User> user = InMemoryUserRepository.findByAccount(requestBodyValues.get("account"));
        if (user.isEmpty() || !user.get().checkPassword(requestBodyValues.get("password"))) {
            response.addResponseHeader(HEADER_LOCATION, UNAUTHORIZED_PAGE);
            return;
        }
        final String sessionId = addSession(user.get());
        response.addResponseHeader(HEADER_LOCATION, INDEX_PAGE);
        response.addResponseHeader(HEADER_SET_COOKIE, "JSESSIONID=" + sessionId);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final HttpCookie cookie = request.getCookie();
        final User user = findUserBySessionId(cookie.getJSessionId());
        if (user != null) {
            doPost(request, response);
            return;
        }
        final var statusLine = StatusLine.of(request.getRequestLine().getProtocol(), HttpStatus.OK);
        final var responseBody = ResponseBody.fromUri("/login.html");
        response.setStatusLine(statusLine);
        response.addResponseHeader(HEADER_CONTENT_TYPE, TEXT_HTML);
        response.addResponseHeader(HEADER_CONTENT_LENGTH, String.valueOf(responseBody.getBody().getBytes().length));
        response.setResponseBody(responseBody);
    }

    private User findUserBySessionId(final String sessionId) {
        if (sessionId == null) {
            return null;
        }
        final Session session = SESSION_MANAGER.findSession(sessionId)
                .orElseGet(Session::create);
        return (User) session.getAttribute("user");
    }

    private String addSession(final User user) {
        final var session = Session.create();
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
        return session.getId();
    }
}
