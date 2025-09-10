package org.apache.coyote.http11.handler;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResourceLoader;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParser;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class LoginHandler implements HttpHandler {

    private final HttpResourceLoader httpResourceLoader;
    private final QueryParser queryParser;
    private final SessionManager sessionManager;

    public LoginHandler(final HttpResourceLoader httpResourceLoader, final QueryParser queryParser,
                        final SessionManager sessionManager) {
        this.httpResourceLoader = httpResourceLoader;
        this.queryParser = queryParser;
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws Exception {
        HttpResponse response = HttpResponse.redirect("/index.html");
        if (request.requestLine().method() == HttpMethod.GET) {
            HttpCookie httpCookie = request.getHttpCookie();
            if (httpCookie.hasSession()) {
                if (sessionManager.hasSession(httpCookie.getSessionId())) {
                    return response;
                }
            }
            return httpResourceLoader.load(request.path());
        }
        request.getHttpCookie().getSession().ifPresent(oldId -> {
            Session old = sessionManager.findSession(oldId);
            if (old != null) {
                sessionManager.remove(old);
            }
        });

        String requestBody = new String(request.body());
        Map<String, String> queriesFromBody = queryParser.parse(requestBody);

        String account = queriesFromBody.get("account");
        String password = queriesFromBody.get("password");

        User user = UserService.login(account, password);

        String sessionId = response.addSessionIfAbsent();
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        return response;
    }
}
