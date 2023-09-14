package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.Session;
import org.apache.coyote.common.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;

public class LoginController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getRequestUri()
                .equals("login");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final Map<String, String> logInfo = Arrays.stream(request.getRequestBody().split("&"))
                .map(input -> input.split("="))
                .collect(Collectors.toMap(info -> info[0], info -> info[1]));

        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.FOUND));
        final Optional<User> savedUser = InMemoryUserRepository.findByAccount(logInfo.get("account"));
        if (savedUser.isPresent()) {
            final User user = savedUser.get();
            if (user.checkPassword(logInfo.get("password"))) {
                response.addHeader("Location", "index.html");
                final Session session = new Session(UUID.randomUUID().toString());
                session.setAttribute("user", user);
                SessionManager.add(session);
                response.addCookie("JSESSIONID", session.getId());
                return;
            }
        }
        response.addHeader("Location", "401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final String sessionId = request.getCookies().ofSessionId("JSESSIONID");
        final Session session = SessionManager.findSession(sessionId);
        if (session != null) {
            response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.FOUND));
            response.addHeader("Location", "index.html");
            return;
        }
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.OK));
        response.addHeader("Content-Type", ContentType.HTML.getType());
        final String content = readResponseBody(request.getRequestUri() + ".html");
        response.setResponseBody(content);
    }
}
