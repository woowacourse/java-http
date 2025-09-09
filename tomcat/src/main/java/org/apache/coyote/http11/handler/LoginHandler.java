package org.apache.coyote.http11.handler;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.util.Map;
import java.util.UUID;
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
    private final HttpCookie httpCookie;
    private final SessionManager sessionManager;

    public LoginHandler(final HttpResourceLoader httpResourceLoader, final QueryParser queryParser,
                        final HttpCookie httpCookie, final SessionManager sessionManager) {
        this.httpResourceLoader = httpResourceLoader;
        this.queryParser = queryParser;
        this.httpCookie = httpCookie;
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws Exception {
        if (request.requestLine().method() == HttpMethod.GET) {
            return httpResourceLoader.load(request.path());
        }
        String sessionId = UUID.randomUUID().toString();
        // TODO: Session 파싱 중복 제거 고려
        Map<String, String> headers = httpCookie.parseCookie(request.headers(), sessionId);

        String requestBody = new String(request.body());
        Map<String, String> queriesFromBody = queryParser.parse(requestBody);

        String account = queriesFromBody.get("account");
        String password = queriesFromBody.get("password");

        User user = UserService.login(account, password);
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        return HttpResponse.redirect("/index.html", headers);
    }
}
