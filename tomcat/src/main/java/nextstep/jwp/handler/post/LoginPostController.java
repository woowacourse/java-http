package nextstep.jwp.handler.post;

import nextstep.jwp.SessionManager;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class LoginPostController implements Controller {

    private static final String STATIC = "static";

    private final SessionManager sessionManager;

    public LoginPostController(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        final RequestBody requestBody = request.getRequestBody();
        final User user = findUser(requestBody);
        if (user == null) {
            final var resource = getClass().getClassLoader().getResource(STATIC + "/401.html");
            setResponse(response, StatusCode.UNAUTHORIZED, ContentType.HTML, resource);
            return;
        }
        final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
        if (request.notContainJsessionId()) {
            final Session session = new Session(String.valueOf(UUID.randomUUID()));
            session.addAttribute("user", user);
            sessionManager.add(session);
            response.addCookie(Cookies.ofJSessionId(session.getId()));
            setResponse(response, StatusCode.FOUND, ContentType.HTML, resource);
            return;
        }
        final String jsessionId = request.findJsessionId();
        final Session session = sessionManager.findSession(jsessionId);
        validateSession(user, session);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        setResponse(response, StatusCode.FOUND, ContentType.HTML, resource);
    }

    private void setResponse(final HttpResponse response, final StatusCode statusCode,
                             final ContentType contentType, final URL resource) throws IOException {
        response.setStatusCode(statusCode);
        response.setContentType(contentType);
        response.setResponseBodyByUrl(resource);
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

    private void validateSession(final User user, final Session session) {
        if (session == null) {
            throw new BusinessException("세션이 적절하지 않습니다.");
        }
        final User sessionUser = (User) session.getAttribute("user");
        if (!user.equals(sessionUser)) {
            throw new BusinessException("세션이 적절하지 않습니다.");
        }
    }
}
