package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public final class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final Optional<User> user = InMemoryUserRepository.findByAccount(request.getRequestBody().get("account"));
        if (user.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }
        if (user.get().checkPassword(request.getRequestBody().get("password"))) {
            log.info("user : {}", user);
            final String sessionId = UUID.randomUUID().toString();
            final Session session = new Session(sessionId);
            session.setAttribute("user", user);
            sessionManager.add(session);

            response.addCookie("JSESSIONID", sessionId);
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Cookie cookie = Cookie.empty();
        if (request.getHttpHeaders().containsHeader("Cookie")) {
            cookie = Cookie.parse(request.getHttpHeaders().getHeaderValue("Cookie"));
        }
        if (cookie.containsKey("JSESSIONID")) {
            response.sendRedirect("/index.html");
            return;
        }

        response.setHttpStatus(HttpStatus.OK);
        final ViewResolver viewResolver = new ViewResolver(Path.of(request.getRequestURI()));
        response.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
        response.write(Files.readString(viewResolver.getResourcePath()));
    }
}
