package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpUri;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.QueryString;
import nextstep.jwp.model.User;
import nextstep.jwp.security.Session;
import nextstep.jwp.security.SessionManager;
import org.apache.catalina.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private static final RequestHandler handler = new LoginHandler();
    private static final Manager sessionManager = SessionManager.getInstance();

    private LoginHandler() {
    }

    public static RequestHandler getInstance() {
        return handler;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            HttpHeaders requestHeaders = request.getHttpHeaders();

            if (requestHeaders.containsKey("Cookie")) {
                HttpCookie cookie = HttpCookie.from(requestHeaders.get("Cookie"));
                String jSessionId = cookie.getJSessionId();
                Session session = sessionManager.findSession(jSessionId);
                User user = (User) session.getAttribute("user");
                InMemoryUserRepository.save(user);

                return redirectToIndexPage(request);
            }

            HttpUri httpUri = request.getHttpUri();
            if (httpUri.hasQueryString()) {
                QueryString queryString = httpUri.getQueryString();

                if (login(queryString)) {
                    return redirectToIndexPage(request);
                }

                return redirectToUnauthorizedPage(request);
            }

            HttpStatus httpStatus = HttpStatus.OK;
            HttpVersion httpVersion = request.getHttpVersion();
            URL url = getClass().getClassLoader().getResource("static/login.html");
            HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
            HttpHeaders httpHeaders = createHeaders(httpBody);

            return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
        }

        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            HttpBody httpBody = request.getHttpBody();
            QueryString queryString = QueryString.from(httpBody.getHttpBody());

            if (login(queryString)) {
                return redirectToIndexPage(request);
            }

            return redirectToUnauthorizedPage(request);
        }

        throw new NoSuchElementException();
    }

    private HttpResponse redirectToUnauthorizedPage(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = HttpBody.from("");
        HttpHeaders httpHeaders = createHeadersWithLocation(httpBody, "/401.html");

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

    private HttpResponse redirectToIndexPage(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = HttpBody.from("");
        HttpHeaders httpHeaders = createHeadersWithLocation(httpBody, "/index.html");

        setCookie(request, httpHeaders);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

    private void setCookie(HttpRequest request, HttpHeaders httpHeaders) {
        HttpHeaders requestHeaders = request.getHttpHeaders();

        if (requestHeaders.containsKey("Cookie")) {
            HttpCookie cookie = HttpCookie.from(requestHeaders.get("Cookie"));
            if (cookie.containsKey("JSESSIONID")) {
                return;
            }
        }

        httpHeaders.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
    }

    private boolean login(QueryString queryString) {
        if (queryString.containsKey("account") && queryString.containsKey("password")) {
            User user = InMemoryUserRepository.findByAccount(queryString.get("account"))
                    .orElseThrow(IllegalArgumentException::new);

            if (user.checkPassword(queryString.get("password"))) {
                log.info("로그인 성공 ! 아이디 : {}", user.getAccount());
                Session session = new Session(UUID.randomUUID().toString());
                session.setAttribute("user", user);
                sessionManager.add(session);

                return true;
            }
        }

        return false;
    }

    private HttpHeaders createHeaders(HttpBody httpBody) {
        List<String> headers = List.of(
                "Content-Type: text/html;charset=utf-8",
                "ContentLength: " + httpBody.getBytesLength()
        );

        return HttpHeaders.from(headers);
    }

    private HttpHeaders createHeadersWithLocation(HttpBody httpBody, String location) {
        List<String> headers = List.of(
                "Content-Type: text/html;charset=utf-8",
                "ContentLength: " + httpBody.getBytesLength(),
                "Location: " + location
        );

        return HttpHeaders.from(headers);
    }

}
