package nextstep.jwp.presentation;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpDataRequest;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.utils.IOUtils;

public class LoginController extends AbstractController {
    private static final String SESSION_KEY = "user";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute(SESSION_KEY);
            if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
                String resource = IOUtils.readResourceFile("/index.html");
                response.setStatusLine(new StatusLine(HttpStatus.FOUND))
                        .setHeaders(ResponseHeaders.create(request, resource))
                        .setResource(resource);
            }
        } catch (NullPointerException e) {
            log.info(e.getMessage(), e);
            String resource = IOUtils.readResourceFile(request.getPath());
            response.setStatusLine(new StatusLine(HttpStatus.OK))
                    .setHeaders(ResponseHeaders.create(request, resource))
                    .setResource(resource);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        var data = HttpDataRequest.extractRequest(request.getRequestBody());
        var user = InMemoryUserRepository.findByAccount(data.get("account"))
                .orElse(null);
        String resource = IOUtils.readResourceFile("/401.html");
        if (Objects.isNull(user)) {
            response.setStatusLine(new StatusLine(HttpStatus.UNAUTHORIZED))
                    .setHeaders(ResponseHeaders.create(request, resource))
                    .setResource(resource);
            return;
        }
        if (user.checkPassword(data.get("password"))) {
            final var session = checkSession(request, user);
            HttpCookie requestCookie = request.getCookie();
            HttpCookie cookie = HttpCookie.ofJSessionId(session.getId());
            String indexResource = IOUtils.readResourceFile("/index.html");

            response.setStatusLine(new StatusLine(HttpStatus.FOUND))
                    .setHeaders(ResponseHeaders.create(request, indexResource))
                    .setResource(indexResource)
                    .addLocation("/index.html")
                    .addCookie(cookie);
            return;
        }
        log.info("user : {}", user);
        response.setStatusLine(new StatusLine(HttpStatus.UNAUTHORIZED))
                .setHeaders(ResponseHeaders.create(request, resource))
                .setResource(resource);
    }

    private HttpSession checkSession(HttpRequest request, User user) {
        SessionManager sessionManager = new SessionManager();
        final var session = request.getSession();
        session.setAttribute("user", user);
        sessionManager.add(session);
        log.info("sessionManager에 저장 : {}", session);
        return session;
    }
}
