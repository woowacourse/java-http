package nextstep.handler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import nextstep.ModelAndView;
import nextstep.View;
import nextstep.ViewResolver;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.util.HttpParser;

public class Handler {

    private static final String INDEX_HTML = "/index.html";
    private static final String BAD_REQUEST_HTML = "/400.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    private Handler() {
    }

    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException {
        ModelAndView modelAndView = handler(httpRequest, httpResponse);
        View view = new View();
        if (modelAndView.getViewName() != null) {
            view = ViewResolver.resolve(modelAndView.getViewName());
        }
        view.render(modelAndView.getModel(), httpResponse);
    }

    private static ModelAndView handler(final HttpRequest httpRequest,
            final HttpResponse httpResponse) {
        final var path = httpRequest.getPath();
        HttpMethod method = httpRequest.getHttpMethod();
        if (method == HttpMethod.GET && path.equals("/")) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setAttribute("message", "Hello world!");
            return modelAndView;
        }
        if (method == HttpMethod.GET && path.isEmpty()) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            return new ModelAndView(INDEX_HTML);
        }
        if (method == HttpMethod.GET && (path.equals("/login") || path.equals("/login.html"))) {
            if (isAlreadyLogin(httpRequest)) {
                httpResponse.setHttpStatus(HttpStatus.FOUND);
                httpResponse.addHeader(HttpHeaders.LOCATION, INDEX_HTML);
                return new ModelAndView(INDEX_HTML);
            }
            httpResponse.setHttpStatus(HttpStatus.OK);
            return new ModelAndView(httpRequest.getPath());
        }
        if (method == HttpMethod.GET && path.endsWith(".html")) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            return new ModelAndView(httpRequest.getPath());
        }
        if (method == HttpMethod.GET) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            return new ModelAndView(httpRequest.getPath());
        }
        if (method == HttpMethod.POST && (path.equals("/login") || path.equals("/login.html"))) {
            final var viewName = login(httpRequest, httpResponse);
            return new ModelAndView(viewName);
        }
        if (method == HttpMethod.POST && (path.equals("/register") || path.equals(
                "/register.html"))) {
            final var viewName = register(httpRequest, httpResponse);
            return new ModelAndView(viewName);
        }
        httpResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
        return new ModelAndView(BAD_REQUEST_HTML);
    }

    private static boolean isAlreadyLogin(final HttpRequest httpRequest) {
        Session session = httpRequest.getSession();
        if (session == null) {
            return false;
        }
        return SessionManager.findSession(session.getId()).isPresent();
    }

    private static String login(HttpRequest httpRequest, HttpResponse httpResponse) {
        final var body = httpRequest.getBody();
        Map<String, String> parameters = HttpParser.parseFormData(body);
        if (parameters.containsKey("account") && parameters.containsKey("password")) {
            return checkUser(httpResponse, parameters.get("account"), parameters.get("password"));
        }
        httpResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
        httpResponse.addHeader(HttpHeaders.LOCATION, BAD_REQUEST_HTML);
        return BAD_REQUEST_HTML;
    }

    private static String checkUser(HttpResponse httpResponse, String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> loginSuccess(httpResponse, user))
                .orElseGet(() -> loginFailed(httpResponse));
    }

    private static String loginSuccess(HttpResponse httpResponse, User user) {
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.addHeader(HttpHeaders.LOCATION, INDEX_HTML);
        final var session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpResponse.addCookie(new HttpCookie(HttpCookie.JSESSIONID, session.getId()));
        return INDEX_HTML;
    }

    private static String loginFailed(HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        httpResponse.addHeader(HttpHeaders.LOCATION, UNAUTHORIZED_HTML);
        return UNAUTHORIZED_HTML;
    }

    private static String register(HttpRequest httpRequest, HttpResponse httpResponse) {
        final String account = "account";
        final String password = "password";
        final String email = "email";

        final var body = httpRequest.getBody();
        Map<String, String> parameters = HttpParser.parseFormData(body);
        if (parameters.containsKey(account) &&
                parameters.containsKey(password) &&
                parameters.containsKey(email)
        ) {
            if (InMemoryUserRepository.findByAccount(parameters.get(account)).isPresent()) {
                httpResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
                httpResponse.addHeader(HttpHeaders.LOCATION, BAD_REQUEST_HTML);
                return BAD_REQUEST_HTML;
            }
            InMemoryUserRepository.save(
                    new User(
                            parameters.get(account),
                            parameters.get(password),
                            parameters.get(email)
                    )
            );

            httpResponse.setHttpStatus(HttpStatus.FOUND);
            httpResponse.addHeader(HttpHeaders.LOCATION, INDEX_HTML);
            return INDEX_HTML;
        }

        httpResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
        httpResponse.addHeader(HttpHeaders.LOCATION, BAD_REQUEST_HTML);
        return BAD_REQUEST_HTML;
    }
}
