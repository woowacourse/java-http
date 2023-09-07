package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemorySessionRepository;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.parser.CookieParser;
import org.apache.coyote.http11.parser.FormDataParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestTarget;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpCookie;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathRequestHandler implements RequestHandler {

    private static final String SESSION_COOKIE_KEY = "JSESSIONID";
    private static final List<String> HANDLEABLE_PATH = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(PathRequestHandler.class);

    private final StaticResourceHandler staticResourceHandler;

    static {
        HANDLEABLE_PATH.add("/");
        HANDLEABLE_PATH.add("/login");
        HANDLEABLE_PATH.add("/register");
    }

    public PathRequestHandler(final StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final HttpRequestTarget target = new HttpRequestTarget(httpRequest.getRequestTarget());

        if (target.getPath().equals("/") && httpRequest.getMethod().equals("GET")) {
            return handleMainPage();
        }

        if (target.getPath().equals("/login") && httpRequest.getMethod().equals("GET")) {
            return handleLoginPage(httpRequest);
        }

        if (target.getPath().equals("/login") && httpRequest.getMethod().equals("POST")) {
            return handleLoginPost(httpRequest);
        }

        if (target.getPath().equals("/register") && httpRequest.getMethod().equals("GET")) {
            return handleRegisterPage();
        }

        if (target.getPath().equals("/register") && httpRequest.getMethod().equals("POST")) {
            return handleRegisterPost(httpRequest);
        }

        return HttpResponse.notFound();
    }

    @Override
    public boolean handleable(final HttpRequest httpRequest) {
        final HttpRequestTarget target = new HttpRequestTarget(httpRequest.getRequestTarget());

        return HANDLEABLE_PATH.contains(target.getPath());
    }

    private HttpResponse handleMainPage() {
        final String body = "Hello world!";

        return HttpResponse.simpleBody(HttpStatusCode.OK, ContentType.HTML_UTF8, body);
    }

    private HttpResponse handleLoginPage(final HttpRequest httpRequest) throws IOException {
        if (isSessionValid(httpRequest)) {
            return staticResourceHandler.handle(
                HttpRequest.withStartLine("GET /index.html HTTP.1,1"));
        }

        return staticResourceHandler.handle(HttpRequest.withStartLine("GET /login.html HTTP.1,1"));
    }

    private boolean isSessionValid(final HttpRequest httpRequest) {
        if (httpRequest.containsHeader("cookie")) {
            final String cookieValue = httpRequest.getHeaderValueIgnoringCase("cookie");
            final Map<String, String> cookieValues = CookieParser.parse(cookieValue);

            if (cookieValues.containsKey(SESSION_COOKIE_KEY)) {
                final String sessionId = cookieValues.get(SESSION_COOKIE_KEY);

                return InMemorySessionRepository.isExist(new Session(sessionId.strip()));
            }
        }
        return false;
    }

    private HttpResponse handleLoginPost(final HttpRequest httpRequest) throws IOException {
        final Map<String, String> formData = FormDataParser.parse(httpRequest.getBody());
        final String account = formData.get("account");
        final String password = formData.get("password");

        final Optional<User> findUser = InMemoryUserRepository.findByAccount(account);

        if (findUser.isPresent() && findUser.get().checkPassword(password)) {
            final User user = findUser.get();
            log.info("login user = {}", user);
            final Optional<Session> findSession = InMemorySessionRepository.findByUser(user);
            return createLoginResponse(findUser.get(), findSession);
        }

        return staticResourceHandler.handle(HttpRequest.withStartLine("GET /401.html HTTP.1,1"));
    }

    private HttpResponse createLoginResponse(final User user, final Optional<Session> session) {
        final HttpResponse response = HttpResponse.redirect("/index.html");
        final String sessionId = session.orElseGet(() -> {
            final Session newUserSession = new Session(UUID.randomUUID().toString());
            return InMemorySessionRepository.save(user, newUserSession);
        }).getUuid();
        response.addCookie(new HttpCookie(Map.of(SESSION_COOKIE_KEY, sessionId)));
        return response;
    }

    private HttpResponse handleRegisterPage() throws IOException {
        return staticResourceHandler.handle(
            HttpRequest.withStartLine("GET /register.html HTTP.1,1"));
    }

    private HttpResponse handleRegisterPost(final HttpRequest httpRequest) {
        final Map<String, String> formData = FormDataParser.parse(httpRequest.getBody());
        final String account = formData.get("account");
        final String email = formData.get("email");
        final String password = formData.get("password");

        //TODO: 회원 가입 성공 여부 검증 로직 필요

        InMemoryUserRepository.save(new User(account, password, email));

        return HttpResponse.redirect("/index.html");
    }
}
