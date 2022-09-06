package nextstep.jwp.handler;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.RequestParser;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;

public class LoginHandler {

    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";

    private static final SessionManager sessionManager = new SessionManager();

    public static HttpResponse perform(HttpRequest request) {
        Map<String, String> queries = RequestParser.parseUri(request.getUri());
        if (request.getMethod().isGet() && queries.isEmpty()) {
            return doGet(request);
        }
        if (request.getMethod().isPost() && queries.isEmpty()) {
            return doPost(request);
        }
        return HttpResponse.notFound();
    }

    private static HttpResponse doGet(HttpRequest request) {
        if (request.hasSession()) {
            String sessionId = request.getCookieValue(JSESSIONID);
            Session session = sessionManager.findSession(sessionId);
            User user = (User) session.getAttribute("user");
            System.out.println(user);
            return HttpResponse.redirect(INDEX_PAGE);
        }
        return ResourceHandler.returnResource(LOGIN_PAGE);
    }

    private static HttpResponse doPost(HttpRequest request) {
        return performLogin(request);
    }

    private static HttpResponse performLogin(HttpRequest request) {
        Optional<User> findUser = InMemoryUserRepository.findByAccount(request.getBodyValue(ACCOUNT));
        User user = findUser.orElseThrow(MemberNotFoundException::new);
        if (!user.checkPassword(request.getBodyValue(PASSWORD))) {
            return HttpResponse.redirect(UNAUTHORIZED_PAGE);
        }

        Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);

        return new HttpResponse.Builder()
                .statusCode(HttpStatus.FOUND)
                .addCookie(session.getId())
                .header(HttpHeaderType.LOCATION, INDEX_PAGE)
                .build();
    }
}
