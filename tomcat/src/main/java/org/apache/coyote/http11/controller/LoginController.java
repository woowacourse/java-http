package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpQueryParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

import static org.apache.coyote.http11.ViewResolver.resolveView;
import static org.apache.coyote.http11.controller.URIPath.INDEX_URI;
import static org.apache.coyote.http11.controller.URIPath.UNAUTHORIZED_URI;
import static org.apache.coyote.http11.types.HeaderType.LOCATION;
import static org.apache.coyote.http11.types.HeaderType.SET_COOKIE;
import static org.apache.coyote.http11.types.HttpStatus.FOUND;

public class LoginController extends AbstractController {

    private static final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> queries = HttpQueryParser.parse(request.getBody());

        String account = queries.get("account");
        String password = queries.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);

        if (user != null && user.checkPassword(password)) {
            HttpCookie cookie = HttpCookie.from(request.getHeader("Cookie"));

            if (cookie.getJSessionId(false) == null) {
                String jSessionId = cookie.getJSessionId(true);
                response.addHeader(SET_COOKIE, String.format("JSESSIONID=%s", jSessionId));
                Session session = new Session(jSessionId);
                session.setAttribute("user", user);
                sessionManager.add(session);
            }

            if (log.isInfoEnabled()) {
                log.info(String.format("%s %s", "로그인 성공!", user));
            }

            response.addHeader(LOCATION, INDEX_URI);
            response.setHttpStatus(FOUND);
            return;
        }

        response.addHeader(LOCATION, UNAUTHORIZED_URI);
        response.setHttpStatus(FOUND);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookie cookie = HttpCookie.from(request.getHeader("Cookie"));
        if (cookie != null && sessionManager.findSession(cookie.getJSessionId(false)) != null) {
            response.addHeader(LOCATION, INDEX_URI);
            response.setHttpStatus(FOUND);
            return;
        }

        resolveView(request, response);
    }
}
