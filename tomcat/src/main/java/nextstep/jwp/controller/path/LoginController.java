package nextstep.jwp.controller.path;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import session.CookieParser;
import session.Session;
import session.SessionManager;
import web.request.HttpRequest;
import web.request.RequestUri;
import web.response.HttpResponse;
import web.response.HttpResponseSetter;
import web.util.QueryStringParser;

public class LoginController extends PathController {

    private static LoginController instance = new LoginController();

    public static LoginController getInstance() {
        if (instance == null) {
            instance = new LoginController();
        }
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String method = httpRequest.getRequestLine().getMethod();
        if (method.equals("GET")) {
            Optional<String> cookieValue = httpRequest.getHeaderValue("Cookie");
            if (cookieValue.isPresent() && CookieParser.checkJSessionIdIsExistInCookieHeader(cookieValue.get())) {
                HttpResponseSetter.set302Redirect(httpResponse, "http://localhost:8080/index.html");
                return;
            }
            HttpResponseSetter.setStaticResource(httpResponse, new RequestUri("/login.html"));
        }
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String method = httpRequest.getRequestLine().getMethod();
        String body = httpRequest.getBody();
        if (method.equals("POST")) {
            login(httpResponse, body);
        }
    }

    private void login(final HttpResponse httpResponse, final String body) {
        Map<String, String> queryString = QueryStringParser.parseQueryString(body);
        String account = queryString.get("account").trim();
        String password = queryString.get("password").trim();
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            HttpResponseSetter.set302Redirect(httpResponse, "http://localhost:8080/index.html");
            enrollJSessionId(httpResponse, user.get());
        } else {
            HttpResponseSetter.set302Redirect(httpResponse, "http://localhost:8080/401.html");
        }
    }

    private void enrollJSessionId(final HttpResponse httpResponse, final User user) {
        Session session = new Session();
        session.addCookie("JSESSIONID", UUID.randomUUID().toString());
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isEmpty()) {
            throw new RuntimeException("[ERROR] 존재하지 않는 User 입니다.");
        }
        SessionManager.addSession(user, session);
        httpResponse.putHeader("Set-Cookie", CookieParser.joinAllCookiesToString(session.getCookies()));
    }

    @Override
    public String getPath() {
        return "/login";
    }
}
