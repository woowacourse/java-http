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
import web.request.RequestLine;
import web.request.RequestUri;
import web.response.HttpResponse;
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
        RequestLine requestLine = httpRequest.getRequestLine();
        String method = requestLine.getMethod();
        if (method.equals("GET")) {
            if (isAlreadyLogin(httpRequest.getHeaderValue("Cookie"))) {
                httpResponse.set302Redirect("http://localhost:8080/index.html");
                return;
            }
            httpResponse.setStaticResource(new RequestUri("/login.html"));
        }
    }

    private boolean isAlreadyLogin(final Optional<String> value) {
        return value.isPresent() && CookieParser.checkJSessionIdIsExistInCookieHeader(value.get());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();
        String method = requestLine.getMethod();
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
        if (isPasswordMatched(password, user)) {
            httpResponse.set302Redirect("http://localhost:8080/index.html");
            enrollJSessionId(httpResponse, user.get());
        } else {
            httpResponse.set302Redirect("http://localhost:8080/401.html");
        }
    }

    private boolean isPasswordMatched(final String password, final Optional<User> user) {
        return user.isPresent() && user.get().checkPassword(password);
    }

    private void enrollJSessionId(final HttpResponse httpResponse, final User user) {
        Session session = new Session();
        session.addCookie("JSESSIONID", UUID.randomUUID().toString());
        SessionManager.addSession(user, session);
        httpResponse.putHeader("Set-Cookie", CookieParser.joinAllCookiesToString(session.getCookies()));
    }

    @Override
    public String getPath() {
        return "/login";
    }
}
