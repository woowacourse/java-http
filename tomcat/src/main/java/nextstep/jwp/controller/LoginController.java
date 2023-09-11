package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.HttpController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.*;

import static org.apache.coyote.http11.common.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.common.HttpHeaderType.SET_COOKIE;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;

public class LoginController extends HttpController {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final Set<String> requestType = Set.of("/login");
        return requestType.contains(httpRequest.getTarget());
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpCookie httpCookie = httpRequest.getCookie();
        String sessionId = httpCookie.getCookie("JSESSIONID");
        if (sessionId != null && SessionManager.getInstance().findSession(sessionId) != null) { // already login user
            httpResponse.addHeader(LOCATION, "/index.html");
            httpResponse.setStatusCode(FOUND);
        } else { // not login user
            handleResource("/login.html", httpRequest, httpResponse);
        }
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final HttpCookie httpCookie = httpRequest.getCookie();
        String sessionId = httpCookie.getCookie("JSESSIONID");

        final Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getBody().get("account"));
        if (user.isEmpty() || !user.get().checkPassword(httpRequest.getBody().get("password"))) {
            // invalid user
            httpResponse.addHeader(LOCATION, "/401.html");
            httpResponse.setStatusCode(FOUND);
            return;
        }

        if (sessionId != null) { // if already have session
            httpResponse.addHeader(LOCATION, "/index.html");
            httpResponse.setStatusCode(FOUND);
            return;
        }

        // if no session
        final Session session = new Session(String.valueOf(UUID.randomUUID()));
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        sessionId = session.getId();

        httpResponse.addHeader(LOCATION, "/index.html");
        httpResponse.addHeader(SET_COOKIE, "JSESSIONID=" + sessionId);
        httpResponse.setStatusCode(FOUND);
    }
}
