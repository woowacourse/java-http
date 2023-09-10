package nextstep.jwp.controller;

import nextstep.jwp.SessionManager;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager;

    public LoginController(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.notContainJsessionId()) {
            response.setStatusCode(StatusCode.OK)
                    .setContentType(ContentType.HTML)
                    .setRedirect("/login.html");
            return;
        }
        final String jsessionId = request.findJsessionId();
        final Session session = sessionManager.findSession(jsessionId);
        validateSession(session);
        response.setStatusCode(StatusCode.FOUND)
                .setContentType(ContentType.HTML)
                .setRedirect("/index.html");
    }

    private void validateSession(final Session session) {
        if (session == null) {
            throw new BusinessException("세션이 적절하지 않습니다.");
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final RequestBody requestBody = request.getRequestBody();
        final User user = findUser(requestBody);
        if (user == null) {
            response.setStatusCode(StatusCode.UNAUTHORIZED)
                    .setContentType(ContentType.HTML)
                    .setRedirect("/401.html");
            return;
        }
        final Session session = createSession(user);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        response.setStatusCode(StatusCode.OK)
                .setContentType(ContentType.HTML)
                .setRedirect("/index.html");
    }

    private User findUser(final RequestBody requestBody) {
        final String[] tokens = requestBody.getValue().split("&");
        final String account = findValueByKey(tokens, "account");
        final String password = findValueByKey(tokens, "password");
        return checkUser(account, password);
    }

    private User checkUser(final String account, final String password) {
        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            return optionalUser.get();
        }
        return null;
    }

    private String findValueByKey(final String[] tokens, final String key) {
        return Arrays.stream(tokens)
                .filter(it -> it.split("=")[0]
                        .equals(key))
                .map(it -> it.split("=")[1])
                .findFirst()
                .orElseThrow(() -> new BusinessException(key + "에 대한 정보가 존재하지 않습니다."));
    }

    private Session createSession(final User user) {
        final Session session = new Session(String.valueOf(UUID.randomUUID()));
        session.addAttribute("user", user);
        sessionManager.add(session);
        return session;
    }
}
