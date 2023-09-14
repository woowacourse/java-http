package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpHeader;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.Session;
import org.apache.coyote.common.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;

public class LoginController extends AbstractController {

    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int ACCOUNT_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getRequestUri()
                .equals("login");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final Map<String, String> authInfo = Arrays.stream(request.getRequestBody().split(PARAM_DELIMITER))
                .map(input -> input.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(info -> info[ACCOUNT_INDEX], info -> info[PASSWORD_INDEX]));

        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.FOUND));
        final Optional<User> savedUser = findUserByLogIn(authInfo);
        if (savedUser.isPresent()) {
            final User user = savedUser.get();
            response.addHeader(HttpHeader.LOCATION.getName(), "index.html");
            final Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            SessionManager.add(session);
            response.addCookie("JSESSIONID", session.getId());
            return;
        }
        response.addHeader(HttpHeader.LOCATION.getName(), "401.html");
    }

    private Optional<User> findUserByLogIn(final Map<String, String> logInfo) {
        final Optional<User> savedUser = InMemoryUserRepository.findByAccount(logInfo.get("account"));
        if (savedUser.isEmpty() || savedUser.get().checkPassword(logInfo.get("password"))) {
            return Optional.empty();
        }
        return savedUser;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final String sessionId = request.getCookies().ofSessionId("JSESSIONID");
        final Session session = SessionManager.findSession(sessionId);
        if (session != null) {
            response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.FOUND));
            response.addHeader(HttpHeader.LOCATION.getName(), "index.html");
            return;
        }
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.OK));
        response.addHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.HTML.getType());
        final String content = readResponseBody(request.getRequestUri() + ContentType.HTML.getExtension());
        response.setResponseBody(content);
    }
}
