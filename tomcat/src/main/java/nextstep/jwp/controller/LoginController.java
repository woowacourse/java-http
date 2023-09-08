package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.http.ContentType;
import org.apache.http.Cookie;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ViewResolver;
import org.apache.http.annotation.Controller;
import org.apache.http.annotation.RequestMapping;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Controller
public final class LoginController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final SessionManager sessionManager = new SessionManager();

    @RequestMapping(method = "POST", path = "/login")
    public void login(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getRequestBody().get("account"));
        if (user.isEmpty()) {
            httpResponse.sendRedirect("/401.html");
            return;
        }
        if (user.get().checkPassword(httpRequest.getRequestBody().get("password"))) {
            log.info("user : {}", user);
            final String sessionId = UUID.randomUUID().toString();
            final Session session = new Session(sessionId);
            session.setAttribute("user", user);
            sessionManager.add(session);

            httpResponse.addCookie("JSESSIONID", sessionId);
            httpResponse.sendRedirect("/index.html");
            return;
        }
        httpResponse.sendRedirect("/401.html");
    }

    @RequestMapping(method = "GET", path = "/login")
    public void loginView(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        Cookie cookie = Cookie.empty();
        if (httpRequest.getHttpHeaders().containsHeader("Cookie")) {
            cookie = Cookie.parse(httpRequest.getHttpHeaders().getHeaderValue("Cookie"));
        }
        if (cookie.containsKey("JSESSIONID")) {
            httpResponse.sendRedirect("/index.html");
            return;
        }

        httpResponse.setHttpStatus(HttpStatus.OK);
        final ViewResolver viewResolver = new ViewResolver(Path.of(httpRequest.getRequestURI()));
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
        httpResponse.write(Files.readString(viewResolver.getResourcePath()));
    }
}
