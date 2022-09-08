package nextstep.jwp.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.http.BasicHttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.info.ContentType;
import org.apache.http.info.HttpMethod;
import org.apache.http.info.HttpVersion;
import org.apache.http.info.StatusCode;
import org.richard.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.annotation.Controller;
import org.springframework.annotation.RequestMapping;
import org.springframework.http.Cookie;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @RequestMapping(method = HttpMethod.GET, uri = "/login")
    public HttpResponse loginPage(final HttpRequest httpRequest) {
        if (isLoggedIn(httpRequest)) {
            return BasicHttpResponse.builder()
                    .httpVersion(HttpVersion.HTTP_1_1)
                    .statusCode(StatusCode.FOUND_302)
                    .redirect("index.html")
                    .build();
        }

        final var loginHtml = String.format("%s.html", httpRequest.getRequestURIWithoutQueryParams());
        final var resource = getClass().getClassLoader().getResource(String.format("static%s", loginHtml));
        final var path = new File(resource.getFile()).toPath();

        logAccountInfo(httpRequest);

        try {
            return BasicHttpResponse.builder()
                    .httpVersion(HttpVersion.HTTP_1_1)
                    .statusCode(StatusCode.OK_200)
                    .contentType(ContentType.TEXT_HTML.getName())
                    .body(new String(Files.readAllBytes(path)))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isLoggedIn(final HttpRequest httpRequest) {
        final var cookie = httpRequest.getHeader("Cookie");
        if (Objects.isNull(cookie)) {
            return false;
        }
        final var splitCookie = cookie.split("=");
        final var jSessionId = splitCookie[1];

        return SessionManager
                .findSession(jSessionId)
                .isPresent();
    }

    private void logAccountInfo(final HttpRequest httpRequest) {
        var account = (String) httpRequest.getParameter("account");
        var password = (String) httpRequest.getParameter("password");
        if (Objects.isNull(account)) {
            account = "";
        }

        InMemoryUserRepository.findByAccountAndPassword(account, password)
                .ifPresent(member -> log.info("found account info : {}", member));
    }

    @RequestMapping(method = HttpMethod.POST, uri = "/login")
    public HttpResponse login(final HttpRequest httpRequest) {
        final Map<String, String> body = httpRequest.getBody();
        final var account = body.getOrDefault("account", "");
        final var password = body.getOrDefault("password", "");

        final var loginSuccess = InMemoryUserRepository
                .findByAccountAndPassword(account, password)
                .isPresent();
        if (loginSuccess) {
            return createLoginSuccessResponse();
        }

        return createLoginFailResponse();
    }

    private BasicHttpResponse createLoginSuccessResponse() {
        final Session session = Session.newInstance();
        final var cookie = Cookie.builder()
                .name("JSESSIONID")
                .value(session.getId())
                .build();

        return BasicHttpResponse.builder()
                .httpVersion(HttpVersion.HTTP_1_1)
                .statusCode(StatusCode.FOUND_302)
                .setCookie(cookie)
                .addHeader("Location", "index.html")
                .build();
    }

    private HttpResponse createLoginFailResponse() {
        return BasicHttpResponse.builder()
                .httpVersion(HttpVersion.HTTP_1_1)
                .statusCode(StatusCode.UNAUTHORIZED_401)
                .contentType(ContentType.TEXT_HTML.getName())
                .body(ResourceUtils.createResourceAsString("static/401.html"))
                .build();
    }
}
