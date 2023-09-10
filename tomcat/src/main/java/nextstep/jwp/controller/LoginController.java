package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.common.header.HttpCookie;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String SUPPORT_URI_PATH = "/login";
    private static final String INDEX_FILE_PATH = "/index.html";
    private static final String LOGIN_FILE_PATH = "/login.html";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public boolean support(final HttpRequest request) {
        return request.isPathOf(SUPPORT_URI_PATH);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.search("account");
        final String password = requestBody.search("password");

        final ResponseEntity responseEntity = InMemoryUserRepository.findByAccount(account)
                                                                    .filter(user -> user.checkPassword(password))
                                                                    .map(this::loginSuccess)
                                                                    .orElseGet(this::loginFail);

        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.hasSessionId()) {
            final String jsessionId = request.findSessionIdFromRequestHeaders(JSESSIONID);

            final ResponseEntity responseEntity = SessionManager.findSession(jsessionId)
                                                                .map(session -> loginSuccess((User) session.getAttribute("user")))
                                                                .orElse(ResponseEntity.of(HttpStatus.OK, LOGIN_FILE_PATH));
            response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));

            return;
        }

        final ResponseEntity responseEntity = ResponseEntity.of(HttpStatus.OK, LOGIN_FILE_PATH);
        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }

    private ResponseEntity loginSuccess(final User findUser) {
        log.info("user: {}", findUser);

        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", findUser);
        SessionManager.add(session);

        final HttpCookie httpCookie = new HttpCookie(Map.of(JSESSIONID, session.getId()));

        return ResponseEntity.of(HttpStatus.FOUND, httpCookie, INDEX_FILE_PATH);
    }

    private ResponseEntity loginFail() {
        final String unauthorizedPath = HttpStatus.UNAUTHORIZED.getResourcePath();

        return ResponseEntity.of(HttpStatus.UNAUTHORIZED, unauthorizedPath);
    }
}
