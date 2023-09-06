package nextstep.jwp.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final SessionManager sessionManager;

    public RequestHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public HttpResponse handle(HttpRequest request) throws IOException {
        String uri = request.getUri();

        if (isIndexPage(request, uri)) {
            return HttpResponse.ok("Hello world!", ContentType.HTML);
        }

        if (uri.equals("/login")) {
            return login(request);
        }

        if (uri.equals("/register")) {
            return signUp(request);
        }

        if (isStaticFile(uri) && request.getMethod() == HttpMethod.GET) {
            System.out.println(uri);
            return getFile(uri);
        }

        return getFile("/404.html");
    }

    private boolean isIndexPage(HttpRequest request, String uri) {
        return uri.equals("/") && request.getMethod() == HttpMethod.GET;
    }

    private HttpResponse getFile(String uri) throws IOException {
        String fileUrl = "static" + uri;
        File file = new File(
            getClass()
                .getClassLoader()
                .getResource(fileUrl)
                .getFile()
        );
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        return HttpResponse.ok(responseBody, ContentType.from(file.getName()));
    }

    private boolean isStaticFile(String target) {
        String value = target.substring(target.lastIndexOf(".") + 1);
        return Arrays.stream(ContentType.values())
            .anyMatch(it -> it.name().equalsIgnoreCase(value));
    }

    private HttpResponse login(HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            return doLoginGetRequest(request);
        }

        Map<String, String> queryString = request.getQueryString();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(queryString.get("account"));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(queryString.get("password"))) {
                log.info(user.toString());
                HttpResponse response = HttpResponse.found("/index.html");
                addCookieAndSession(response, user);
                return response;
            }
        }

        return HttpResponse.found("/401.html");
    }

    private HttpResponse doLoginGetRequest(HttpRequest request) throws IOException {
        if (isLogin(request)) {
            return HttpResponse.found("/index.html");
        }

        return getFile("/login.html");
    }

    private boolean isLogin(HttpRequest request) {
        Map<String, String> cookie = request.getCookie();
        if (cookie.get("JSESSIONID") != null) {
            String sessionId = cookie.get("JSESSIONID");
            Session session = sessionManager.findSession(sessionId);
            if (session.getAttribute("user") != null) {
                return true;
            }
        }
        return false;
    }

    private void addCookieAndSession(HttpResponse response, User user) {
        String uuid = UUID.randomUUID().toString();
        Session session = SessionManager.createSession(uuid);
        session.setAttribute("user", user);
        response.getCookie().put("JSESSIONID", uuid);
    }

    private HttpResponse signUp(HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            return getFile("/register.html");
        }
        Map<String, String> body = Arrays.stream(request.getBody().split("&"))
            .map(it -> it.split("="))
            .collect(Collectors.toMap(
                keyAndValue -> keyAndValue[0],
                keyAndValue -> keyAndValue[1]));
        InMemoryUserRepository.save(new User(
            body.get("account"),
            body.get("password"),
            body.get("email")
        ));
        return HttpResponse.found("/index.html");
    }
}
